##Поиск багов
### Запуск
Без нагрузки память в течении двух минут доходит до 25 МБ и очищается, CPU ожидаемо - 0
После запуска скрипта нагрузка на процессор колеблется на уровне 20% процентов с небольшой тенденцией к росту.
Использование памяти растет с уровня 25МБ. Eden чиститься примерно каждую секунду, Old не очищается. На GC тратиться 0.1 % CPU. Через 8 минут на 3300 итерации GC очищает Old при достижении 60 МБ
100 0.116
500 0.095
1000 0.099
1500 0.115
3000 0.182

### Ненужный вызов AccountDAOImpl.getAllAcounts
Профайлер подсказывает, что нужно обратить внимание в первую очередь на методы getAllAccounts класса AccountDAOImpl и equals класса Account. Просматривая стек вызовов, видим, что как раз из метода getAllAccounts метод equals вызывается чрезмерное количество раз. Также по стеку можно увидеть, что при каждом вызове метода getAccountByUser вызывается метод getAllAccounts, что уже кажется нелогичным. В исходном коде сомнения подтвердились, внутри метода getAccountByUser обнаруживаем бесполезный вызов метода getAllAccounts, результат которого отфильтровывается с применением метода equals. Уберем этот код.

100 0.123
500 0.107
1000 0.107
1500 0.117
3000 0.140
4000 0.165

### Поломанный hashCode
На первый взгляд ничего не изменилось, но падение скорости сценария замедлилось.
Преимущественно показатели аналогичны показателям до удаления бага, но CPU теперь порядка 15%, тенденция к росту сохраняется.
Профайлер также указывает, что основной потребитель CPU - AccountDAOImpl.getAllAcounts. А его самый требовательный вызов по-прежнему Account.equals. Изучение класса Account дало плоды, в классе переопределен метод hashCode. Так как AccountDAOImpl.getAllAcounts оперирует с HashSet, этот момент критичен. Просто удалим переопределенный метод, полагая, что исходный метод Object наверняка лучше справится с задачей.

100 0.112
500 0.093
1000 0.114
1500 0.111
3000 0.108
4000 0.111
6000 0.125

### Еще про hashCode и неиспользуемые переменные
CPU - 10%. Полный сбор мусора спустя 5 минут при достижении 70 МБ, следующий через три минуты при достижении 90 МБ. В целом использование памяти колеблется в промежутке 50-70МБ. При уменьшении нагрузки на процессор утечка памяти стала более явной.
Теперь есть несколько методов, которые потребляют процессорное время примерно на одном уровне. Попробуем найти утечку памяти в коде этих методов. Судя по HeapDump большая часть памяти уходит на char[]. При этом вероятно утечку должна брать на себя коллекция или какой-то другой мутирующий объект, который постоянно увеличивается и нигде не используется или используется нерационально. Вероятно на него должна ссылаться статическая переменная или переменная класса.
Просматривая класс User, обнаружили метод hashCode. Попробуем инкорпорировать его в Account, может быть он неплохо себя проявит.

100 0.111
500 0.1
3000 0.098
6000 0.118

Замена hashCode дала свои плоды. Сократилось среднее время на сценарий.

В классе UserService обнаружилась неиспользуемая переменная allUsers, что бесконечно копит в себе списки user. Удалили. То же самое с UserDAOImpl.fetched.

100 0.106
500 0.093
1000 0.089
1500 0.091
3000 0.098
4000 0.103
6000 0.118

Сценарий немного ускорился, но память продолжает расти, но и заметно медленнее.

##Анализ GC
Показатели после 5000 итераций.

###Serial

Min: 5 MB
Max: 45 MB
Avg: ~25 MB
За исследуемое время не было Full GC
Avg. time: 0.108 с
CPU: первые минута ~0.0%, потом плавно увеличился до 0.7%, без пиков.

###Parallel

Min: 5 MB
Max: 81 MB
Avg: ~50 MB
Первый Full GC через 7 минут
После первой полной очистки: 20 MB
Avg. time: 0.112 с
CPU: первые три минуты ~0.0%, потом плавно увеличивается до 0.2%, с очень редкими пиками до 4%.

###CMS

Min: 8 MB
Max: 42 MB
Avg: ~25 MB
За исследуемое время не было Full GC
Avg. time: 0.113 с
CPU: первая минута ~0.0%, потом плавно увеличивается до 0.4%, без пиков.

###G1

Min: 13 MB
Max: 345 MB
Avg: ~80 MB
G1 не совершает Full GC в привычном виде
Avg. time: 0.115 с
CPU: первые три минуты ~0.0%, потом плавно увеличивается до 0.7%, при этом иногда уходит в ноль.

STW Pause не произошла ни в одном из случаев.
По совокупности факторов CMS кажется наиболее предпочтительным вариантом. Он обладает Низким потреблением памяти, невысокой нагрузкой на процессор и потенциально не приводит к STW.

import requests
import time
from functools import wraps

host = 'http://localhost:53609/'

PROF_DATA = {}


def profile(fn):
    @wraps(fn)
    def with_profiling(*args, **kwargs):
        start_time = time.time()

        ret = fn(*args, **kwargs)

        elapsed_time = time.time() - start_time

        if fn.__name__ not in PROF_DATA:
            PROF_DATA[fn.__name__] = [0, {'last': 0.0, 'avg': 0.0}]
        PROF_DATA[fn.__name__][0] += 1
        PROF_DATA[fn.__name__][1]['last'] = elapsed_time
        PROF_DATA[fn.__name__][1]['avg'] = ((PROF_DATA[fn.__name__][0]-1)*PROF_DATA[fn.__name__][1]['avg'] + elapsed_time) / PROF_DATA[fn.__name__][0]

        return ret

    return with_profiling


def print_prof_data():
    for fname, data in PROF_DATA.items():
        print("Function %s called %d times. " % (fname, data[0]))
        print('Execution time last: %.3f, average: %.3f' % (data[1]['last'], data[1]['avg']))


def clear_prof_data():
    global PROF_DATA
    PROF_DATA = {}


def get_user(user_name):
    return requests.get(host + 'user/' + user_name, headers={'Connection':'close'}).json()


def get_accounts():
    return requests.get(host + 'account/all', headers={'Connection':'close'}).json()


def find_account(user_name):
    return requests.get(host + 'account/by_user/' + user_name + '/USD', headers={'Connection':'close'}).json()


def create_new_user(user_name, email):
    return requests.post(url=host + 'user/create', json={'userName': user_name, 'emailAddress': email}, headers={'Connection':'close'}).json()


def create_new_account(user_name, balance):
    return requests.post(url=host + 'account/create', json={'userName': user_name, 'balance': balance, 'currencyCode': 'USD'}, headers={'Connection':'close'}).json()


def deposit_money(account_id, amount):
    return requests.put(host + 'account/' + str(account_id) + '/deposit/' + str(amount), headers={'Connection':'close'}).json()


def withdraw_money(account_id, amount):
    return requests.put(host + 'account/' + str(account_id) + '/withdraw/' + str(amount), headers={'Connection':'close'}).json()


def delete_account(account_id):
    return requests.delete(host + 'account/' + str(account_id), headers={'Connection':'close'})


def transfer(amount, from_id, to_id):
    return requests.post(host + '/transaction', json={'currencyCode': 'USD', 'amount': amount, 'fromAccountId': from_id, 'toAccountId': to_id}, headers={'Connection':'close'})


def get_all_users():
    return requests.get(url=host + 'user/all', headers={'Connection':'close'}).json()


@profile
def get_user_image():
    return requests.get(url=host + 'user/1/image', headers={'Connection':'close'}).content


@profile
def play_scenario_1(user_name):
    create_new_user(user_name, user_name + '@mail.com')
    acc = create_new_account(user_name, 100)
    deposit_money(acc['accountId'], 200)
    transfer(250, acc['accountId'], find_account('yangluo')['accountId'])
    withdraw_money(acc['accountId'], 50)
    print()
    print('users: ' + str(len(get_all_users())))
    print('accounts: ' + str(len(get_accounts())))
    print('yangluo money: ' + str(find_account('yangluo')['balance']))


for i in range(1000000):
    play_scenario_1("Mikel" + str(i))
    print_prof_data()



set -x
recd="600s" # recording duration
rdel="20s" # recording delay

lsofline=`lsof -i:8080 | grep java --color=never`
lsof_regex="java\s*(\S*)"
if [[ $lsofline =~ $lsof_regex ]]
	then
		oldjpid="${BASH_REMATCH[1]}"
		echo "oldjpid = $oldjpid"
		kill $oldjpid
		echo "ok, I killed it"
		# exit 1
		
else
	echo "port 8080 is free"
fi

rm ./*.jfr
rm ./*.plg


stage=""
gitline=`git log --oneline --decorate | grep HEAD`
git_regex="tag: ([^,)]*)"
if [[ $gitline =~ $git_regex ]]
	then
		stage="${BASH_REMATCH[1]}"
		echo $stage
else
	echo "cant' determinate stage"
	exit 1
fi

java -XX:+UnlockCommercialFeatures \
	 -XX:+FlightRecorder \
	 -XX:+StartAttachListener \
	 -jar "./target/money-transfer-1.0.0-SNAPSHOT-shaded.jar" &>/tmp/money &
sleep 4
jpid=$!
echo "Java pid = $jpid"
jfrpath="rec_d-${recd}_s-${stage}.jfr"
logpath="log_d-${recd}_s-${stage}.plg"
echo "JFR path: " `pwd`$jfrpath
echo "start at " `date +%H:%M:%S`
jcmd $jpid JFR.start duration=$recd delay=$rdel filename="$jfrpath" &>/tmp/jcmd
python3 "run_tests.py" &>$logpath &
ppid=$!
echo "Python pid = $ppid"
sleep $recd
sleep $rdel
sleep 10 # guard interval
kill  $ppid
kill  $jpid
echo "ready"
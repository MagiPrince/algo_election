#!/bin/bash

osascript <<END
tell application "Terminal"
    do script "ssh david.nogueira1@129.194.184.115 \"cd Bureau/algo_election/Public; java Election 10001 neighbours/neighbours-0.txt WAIT\""
end tell
tell application "Terminal"
    do script "ssh david.nogueira1@129.194.184.116 \"cd Bureau/algo_election/Public; java Election 10001 neighbours/neighbours-1.txt WAIT\""
end tell
tell application "Terminal"
    do script "ssh david.nogueira1@129.194.184.117 \"cd Bureau/algo_election/Public; java Election 10001 neighbours/neighbours-2.txt WAIT\""
end tell
tell application "Terminal"
    do script "ssh david.nogueira1@129.194.184.118 \"cd Bureau/algo_election/Public; java Election 10001 neighbours/neighbours-3.txt INIT\""
end tell
tell application "Terminal"
    do script "ssh david.nogueira1@129.194.184.119 \"cd Bureau/algo_election/Public; java Election 10001 neighbours/neighbours-4.txt INIT\""
end tell
END
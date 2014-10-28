#!/bin/sh -e
sed -i '/#/! s/\$str \.= "refuse-pap/#\$str \.= "refuse-pap/g' /opt/vyatta/share/perl5/Vyatta/PPPoEServerConfig.pm 
sed -i '/#/! s/\$str \.= "refuse-chap/#\$str \.= "refuse-chap/g' /opt/vyatta/share/perl5/Vyatta/PPPoEServerConfig.pm
sed -i '/#/! s/\$str \.= "refuse-mschap/#\$str \.= "refuse-mschap/g' /opt/vyatta/share/perl5/Vyatta/PPPoEServerConfig.pm 
sed -i '/#/! s/\$str \.= "require-mschap-v2/#\$str \.= "require-mschap-v2/g' /opt/vyatta/share/perl5/Vyatta/PPPoEServerConfig.pm 

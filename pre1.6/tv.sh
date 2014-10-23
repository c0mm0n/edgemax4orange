#!/bin/sh -e
/sbin/vconfig set_egress_map eth1.840 0 5
/sbin/vconfig set_egress_map eth1.838 0 4
/sbin/vconfig set_egress_map eth1.840 1 5
/sbin/vconfig set_egress_map eth1.838 1 4
/sbin/vconfig set_egress_map eth1.840 2 5
/sbin/vconfig set_egress_map eth1.838 2 4
/sbin/vconfig set_egress_map eth1.840 3 5
/sbin/vconfig set_egress_map eth1.838 3 4
/sbin/vconfig set_egress_map eth1.840 4 5
/sbin/vconfig set_egress_map eth1.838 4 4
/sbin/vconfig set_egress_map eth1.840 5 5
/sbin/vconfig set_egress_map eth1.838 5 4
/sbin/vconfig set_egress_map eth1.840 6 5
/sbin/vconfig set_egress_map eth1.838 6 4
/sbin/vconfig set_egress_map eth1.840 7 5
/sbin/vconfig set_egress_map eth1.838 7 4
/sbin/vconfig set_egress_map eth1.851 0 6
/sbin/vconfig set_egress_map eth1.851 1 6
/sbin/vconfig set_egress_map eth1.851 2 6
/sbin/vconfig set_egress_map eth1.851 3 6
/sbin/vconfig set_egress_map eth1.851 6 6
/sbin/vconfig set_egress_map eth1.851 5 6
/sbin/vconfig set_egress_map eth1.851 6 6
/sbin/vconfig set_egress_map eth1.851 7 6
dhclient -cf /config/dhclient.conf br0
/sbin/start-stop-daemon --start --startas /sbin/igmpproxy --make-pidfile --pidfile /var/run/igmpproxy.pid --background -- /etc/igmpproxy.conf
exit 0
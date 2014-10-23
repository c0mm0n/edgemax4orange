<?

if(
        empty($_GET['login'])
        OR empty($_GET['pwd'])
        OR empty($_GET['gateway'])
        OR empty($_GET['mac'])


    ){
    exit("Un des champs n'est pas rempli.");
}


// CALCUL DE LA MAC
$mac = explode(':', $_GET['mac']);

if (sizeof($mac) != 6){
    exit('Adresse Mac invalide.');
}

$from = '0x'.$mac[sizeof($mac)-1];

$from = $from+0x4;
$from = dechex($from);

$mac[5] = sprintf("%02d", $from);

//$amac = implode(':', $mac);
$amac = str_pad(implode(':', $mac), 2, "0", STR_PAD_LEFT);
$login =  $_GET['login'];
$pwd =  $_GET['pwd'];
$gateway =  $_GET['gateway'];

$file = $_SERVER['REQUEST_URI'];

if ($_GET['download'] == 1){
    header('Content-Type: application/force-download;charset=ISO-8859-1; name="orange.boot"'); 
    header("Content-Transfer-Encoding: binary"); 
    header('Content-Disposition: attachment; filename="orange.boot"'); 
}


if ($_GET['download'] != 1){
    echo "<pre>";
}
?>

firewall {
    all-ping enable
    broadcast-ping disable
    ipv6-receive-redirects disable
    ipv6-src-route disable
    ip-src-route disable
    log-martians enable
    name WAN_IN {
        default-action drop
        description "packets from Internet to LAN"
        enable-default-log
        rule 1 {
            action accept
            description "allow established sessions"
            log disable
            protocol all
            state {
                established enable
                invalid disable
                new disable
                related enable
            }
        }
        rule 2 {
            action drop
            description "drop invalid state"
            log disable
            protocol all
            state {
                established disable
                invalid enable
                new disable
                related disable
            }
        }
    }
    name WAN_LOCAL {
        default-action drop
        description "packets from Internet to the router"
        rule 1 {
            action accept
            description "allow established session to the router"
            log disable
            protocol all
            state {
                established enable
                invalid disable
                new disable
                related enable
            }
        }
        rule 2 {
            action drop
            description "drop invalid state"
            log disable
            protocol all
            state {
                established disable
                invalid enable
                new disable
                related disable
            }
        }
    }
    options {
        mss-clamp {
            mss 1452
        }
    }
    receive-redirects disable
    send-redirects enable
    source-validation disable
    syn-cookies enable
}
interfaces {
    bridge br0 {
        address dhcp
        aging 300
        dhcp-options {
            client-option "send vendor-class-identifier &quot;sagem&quot;;"
            client-option "send dhcp-client-identifier 1:<?php echo $amac?>;"
            client-option "send user-class &quot;\047FSVDSL_livebox.MLTV.softathome.Livebox3&quot;;"
            default-route update
            name-server update
        }
        hello-time 2
        max-age 20
        priority 0
        promiscuous disable
        stp false
    }
    ethernet eth0 {
        address 192.168.1.1/24
        description LAN1
        duplex auto
        speed auto
    }
    ethernet eth1 {
        description Internet
        duplex auto
        speed auto
        vif 835 {
            address dhcp
            description FTTH
            pppoe 0 {
                default-route auto
                firewall {
                    in {
                        name WAN_IN
                    }
                    local {
                        name WAN_LOCAL
                    }
                }
                mtu 1492
                name-server auto
                user-id fti/<?php echo $login?> 
                password <?php echo $pwd?> 
                 
            }
        }
        vif 838 {
            bridge-group {
                bridge br0
            }
            description TV
            egress-qos "0:4 1:4 2:4 3:4 4:4 5:4 6:4 7:4"
        }
        vif 840 {
            bridge-group {
                bridge br0
            }
            description TV
            egress-qos "0:5 1:5 2:5 3:5 4:5 5:5 6:5 7:5"
        }
        vif 851 {
            description VoIP
            egress-qos "0:6 1:6 2:6 3:6 5:6 6:6 7:6"
        }
    }
    ethernet eth2 {
        address 192.168.2.1/24
        description LAN2
        duplex auto
        speed auto
    }
    loopback lo {
    }
}

protocols {
    igmp-proxy {
        disable-quickleave
        interface br0 {
            alt-subnet 0.0.0.0/0
            role upstream
            threshold 1
        }
        interface eth0 {
            alt-subnet 0.0.0.0/0
            role downstream
            threshold 1
        }
        interface eth2 {
            alt-subnet 0.0.0.0/0
            role downstream
            threshold 1
        }
    }
    static {
        route 80.10.117.120/31 {
            next-hop <?php echo $gateway?> {
            }
        }
        route 81.253.206.0/24 {
            next-hop <?php echo $gateway?> {
            }
        }
        route 81.253.210.0/23 {
            next-hop <?php echo $gateway?> {
            }
        }
        route 81.253.214.0/23 {
            next-hop <?php echo $gateway?> {
            }
        }
        route 172.19.20.0/23 {
            next-hop <?php echo $gateway?> {
            }
        }
        route 172.20.224.167/32 {
            next-hop <?php echo $gateway?> {
            }
        }
        route 172.23.12.0/22 {
            next-hop <?php echo $gateway?> {
            }
        }
        route 193.253.67.88/29 {
            next-hop <?php echo $gateway?> {
            }
        }
        route 193.253.153.227/32 {
            next-hop <?php echo $gateway?> {
            }
        }
        route 193.253.153.228/32 {
            next-hop <?php echo $gateway?> {
            }
        }
    }
}
service {
    dhcp-server {
        disabled false
        hostfile-update disable
        shared-network-name LAN1 {
            authoritative disable
            subnet 192.168.1.0/24 {
                default-router 192.168.1.1
                dns-server 192.168.1.1
                lease 86400
                start 192.168.1.2 {
                    stop 192.168.1.200
                }
            }
        }
        shared-network-name LAN2 {
            authoritative enable
            subnet 192.168.2.0/24 {
                default-router 192.168.2.1
                dns-server 192.168.2.1
                lease 86400
                start 192.168.2.21 {
                    stop 192.168.2.200
                }
            }
        }
    }
    dns {
        forwarding {
            cache-size 1000
            listen-on eth2
            listen-on eth0
        }
    }
    gui {
        https-port 443
    }
    mdns {
        reflector
    }
    nat {
        rule 5010 {
            outbound-interface pppoe0
            type masquerade
        }
        rule 5011 {
            outbound-interface br0
            type masquerade
        }
    }
    ssh {
        port 22
        protocol-version v2
    }
    upnp2 {
        listen-on eth0
        listen-on eth2
        nat-pmp enable
        secure-mode disable
        wan pppoe0
    }
}
system {
    config-management {
        commit-revisions 50
    }
    host-name ubnt
    login {
        user ubnt {
            authentication {
                encrypted-password $1$zKNoUbAo$gomzUbYvgyUMcD436Wo66.
            }
            level admin
        }
    }
    name-server 8.8.8.8
    name-server 8.8.4.4
    ntp {
        server 0.ubnt.pool.ntp.org {
        }
        server 1.ubnt.pool.ntp.org {
        }
        server 2.ubnt.pool.ntp.org {
        }
        server 3.ubnt.pool.ntp.org {
        }
    }
    offload {
        ipv4 {
            forwarding enable
            pppoe enable
            vlan enable
        }
    }
    package {
        repository wheezy {
            components "main contrib non-free"
            distribution wheezy
            password ""
            url http://http.us.debian.org/debian
            username ""
        }
        repository wheezy-security {
            components main
            distribution wheezy/updates
            password ""
            url http://security.debian.org
            username ""
        }
    }
    syslog {
        global {
            facility all {
                level notice
            }
            facility protocols {
                level warning
            }
        }
    }
    time-zone Europe/Paris
}


/* Warning: Do not remove the following line. */
/* === vyatta-config-version: "config-management@1:conntrack@1:cron@1:dhcp-relay@1:dhcp-server@4:firewall@5:ipsec@4:nat@3:qos@1:quagga@2:system@4:ubnt-pptp@1:ubnt-util@1:vrrp@1:webgui@1:webproxy@1:zone-policy@1" === */
/* Release version: v1.6.0beta1.4705702.140925.2253 */
<?
if ($_GET['download'] != 1){
    echo "</pre>";
}


?>
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
            interface-type pppoe
            interface-type pptp
            interface-type tun
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
        aging 300
        hello-time 2
        max-age 20
        priority 0
        promiscuous disable
        stp false
    }
    ethernet eth0 {
        address 192.168.2.1/24
        description LAN2
        duplex auto
        poe {
            output off
        }
        speed auto
        vif 835 {
        }
        vif 838 {
            bridge-group {
                bridge br0
            }
            description TV
        }
        vif 840 {
            bridge-group {
                bridge br0
            }
            description TV
        }
        vif 851 {
            bridge-group {
                bridge br0
            }
            description VoIP
        }
    }
    ethernet eth1 {
        description Internet
        duplex auto
        poe {
            output off
        }
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
                password YOUR_PASSWORD
                user-id fti/YOUR_ID
            }
        }
        vif 838 {
            bridge-group {
                bridge br0
            }
            description TV
        }
        vif 840 {
            bridge-group {
                bridge br0
            }
            description TV
        }
        vif 851 {
            bridge-group {
                bridge br0
            }
            description VoIP
        }
    }
    ethernet eth2 {
        duplex auto
        poe {
            output off
        }
        speed auto
    }
    ethernet eth3 {
        duplex auto
        poe {
            output off
        }
        speed auto
    }
    ethernet eth4 {
        duplex auto
        poe {
            output off
        }
        speed auto
    }
    loopback lo {
    }
    switch switch0 {
        address 192.168.0.1/24
        description LAN1
        mtu 1500
        switch-port {
            interface eth2
            interface eth3
            interface eth4
        }
    }
}

service {
    dhcp-server {
        disabled false
        hostfile-update disable
        shared-network-name LAN1 {
            authoritative disable
            subnet 192.168.0.1/24 {
                default-router 192.168.0.1
                dns-server 8.8.8.8
                dns-server 8.4.4.4
                lease 86400
                start 192.168.0.50 {
                    stop 192.168.0.200
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
            listen-on switch0
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
    }
    pppoe-server {
        authentication {
            local-users {
                username fti/YOUR_ID {
                    password YOUT_PASSWORD
                }
            }
            mode local
        }
        client-ip-pool {
            start 192.168.2.210
            stop 192.168.2.220
        }
        dns-servers {
            server-1 80.10.246.2
            server-2 80.10.246.129
        }
        interface eth0.835
        mtu 1492
    }
    ssh {
        port 22
        protocol-version v2
    }
    upnp {
    }
    upnp2 {
        listen-on eth0
        listen-on switch0
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
            full-name ""
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
/* Release version: v1.6.0.4716006.141031.1731 */

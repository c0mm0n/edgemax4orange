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
        address dhcp
        aging 300
        description "WAN VIDEO"
        dhcp-options {
            client-option "send vendor-class-identifier &quot;sagem&quot;;"
            client-option "send user-class &quot;\047FSVDSL_livebox.MLTV.softathome.Livebox3&quot;;"
            client-option "send dhcp-client-identifier 1:00:00:00:00:00:00;"
            default-route update
            default-route-distance 210
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
        description "LAN DEV"
        duplex auto
        ip {
        }
        poe {
            output off
        }
        speed auto
        vif 835 {
            description "LAN PPPoE"
        }
    }
    ethernet eth1 {
        description WAN
        duplex auto
        poe {
            output off
        }
        speed auto
        vif 835 {
            description "WAN FTTH"
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
                user-id fti/user
                password secret
            }
        }
        vif 838 {
            bridge-group {
                bridge br0
            }
            description "WAN TV"
            egress-qos "0:4 1:4 2:4 3:4 4:4 5:4 6:4 7:4"
        }
        vif 840 {
            bridge-group {
                bridge br0
            }
            description "WAN TV"
            egress-qos "0:5 1:5 2:5 3:5 4:5 5:5 6:5 7:5"
        }
        vif 851 {
            description "WAN VoIP"
            egress-qos "0:6 1:6 2:6 3:6 4:6 5:6 6:6 7:6"
            mtu 1500
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
        address 192.168.2.1/24
        description LAN
        mtu 1500
        switch-port {
            interface eth2
            interface eth3
            interface eth4
        }
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
            role disabled
            threshold 1
        }
        interface switch0 {
            alt-subnet 0.0.0.0/0
            role downstream
            threshold 1
        }
    }
    static {
        route 80.10.117.120/31 {
            next-hop 0.0.0.0 {
            }
        }
        route 81.253.206.0/24 {
            next-hop 0.0.0.0 {
            }
        }
        route 81.253.210.0/23 {
            next-hop 0.0.0.0 {
            }
        }
        route 81.253.214.0/23 {
            next-hop 0.0.0.0 {
            }
        }
        route 172.19.20.0/23 {
            next-hop 0.0.0.0 {
            }
        }
        route 172.20.224.167/32 {
            next-hop 0.0.0.0 {
            }
        }
        route 172.23.12.0/22 {
            next-hop 0.0.0.0 {
            }
        }
        route 193.253.67.88/29 {
            next-hop 0.0.0.0 {
            }
        }
        route 193.253.153.227/32 {
            next-hop 0.0.0.0 {
            }
        }
        route 193.253.153.228/32 {
            next-hop 0.0.0.0 {
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
                start 192.168.1.21 {
                    stop 192.168.1.240
                }
            }
        }
        shared-network-name LAN2 {
            authoritative enable
            subnet 192.168.2.0/24 {
                default-router 192.168.2.1
                dns-server 192.168.2.1
                lease 86400
            }
        }
        shared-network-name LAN3 {
            authoritative disable
            subnet 192.168.9.0/24 {
                default-router 192.168.9.1
                dns-server 192.168.9.1
                lease 86400
            }
        }
    }
    dns {
        forwarding {
            cache-size 0
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

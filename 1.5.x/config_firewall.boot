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
        enable-default-log
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
            mss 1412
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
        address 192.168.1.1/24
        description Local
        duplex auto
        speed auto
    }
    ethernet eth1 {
        description "Internet (PPPoE)"
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
                password rabzecg
                user-id fti/fw2cghy
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
            address dhcp
            description TOIP
        }
    }
    ethernet eth2 {
        address 192.168.2.1/24
        description "Local 2"
        duplex auto
        speed auto
    }
    loopback lo {
    }
}
port-forward {
    auto-firewall enable
    hairpin-nat enable
    lan-interface eth2
    lan-interface eth0
    rule 1 {
        description "VPN PPTP"
        forward-to {
            address 192.168.2.2
            port 1723
        }
        original-port 1723
        protocol tcp_udp
    }
    wan-interface pppoe0
}
protocols {
    igmp-proxy {
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
            next-hop 10.54.56.254 {
            }
        }
        route 81.253.206.0/24 {
            next-hop 10.54.56.254 {
            }
        }
        route 81.253.210.0/23 {
            next-hop 10.54.56.254 {
            }
        }
        route 81.253.214.0/23 {
            next-hop 10.54.56.254 {
            }
        }
        route 172.19.20.0/23 {
            next-hop 10.54.56.254 {
            }
        }
        route 172.20.224.167/32 {
            next-hop 10.54.56.254 {
            }
        }
        route 172.23.12.0/22 {
            next-hop 10.54.56.254 {
            }
        }
        route 193.253.67.88/29 {
            next-hop 10.54.56.254 {
            }
        }
        route 193.253.153.227/32 {
            next-hop 10.54.56.254 {
            }
        }
        route 193.253.153.228/32 {
            next-hop 10.54.56.254 {
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
                start 192.168.2.21 {
                    stop 192.168.2.240
                }
                static-mapping CAM {
                    ip-address 192.168.2.199
                    mac-address 00:62:6e:4f:c0:fc
                }
            }
        }
    }
    dns {
        forwarding {
            cache-size 150
            listen-on eth2
            listen-on eth0
        }
    }
    gui {
        https-port 443
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
        user c0mm0n {
            authentication {
                encrypted-password $6$r.C1USw7/Z$iJq/3kwxFNb/dF7CPJ3gJnHepKRsWaVgwMGQJTRVwm9snb5NMPYdcYrVm8F8ttGGnucPSoU0eYhY5FraFNVf2/
                plaintext-password ""
            }
            full-name c0mm0n
            level admin
        }
        user ubnt {
            authentication {
                encrypted-password $1$zKNoUbAo$gomzUbYvgyUMcD436Wo66.
            }
            level admin
        }
    }
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
            vlan disable
        }
    }
    package {
        repository squeeze {
            components "main contrib non-free"
            distribution squeeze
            password ""
            url http://http.us.debian.org/debian
            username ""
        }
        repository squeeze-security {
            components main
            distribution squeeze/updates
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
                level debug
            }
        }
        host 192.168.2.2 {
            facility all {
                level notice
            }
        }
    }
    time-zone UTC
}


/* Warning: Do not remove the following line. */
/* === vyatta-config-version: "config-management@1:conntrack@1:cron@1:dhcp-relay@1:dhcp-server@4:firewall@5:ipsec@3:nat@3:qos@1:quagga@2:system@4:ubnt-pptp@1:ubnt-util@1:vrrp@1:webgui@1:webproxy@1:zone-policy@1" === */
/* Release version: v1.5.0alpha1.4653508.140328.1720 */

## Edgemax Setup For Orange France FTTH

> French version and support of this guide is here
 : https://lafibre.info/orange-tutoriels/en-cours-remplacer-sa-livebox-par-un-routeur-ubiquiti-edgemax/

### Overview
***
This setup will help you setup an edgemax router for french ISP Orange with
FTTH.

Following this tutorial you'll have internet and tv working including replays
and vod. Other services like gaming, radios are also reported to work.

> Phone will not work but we have a trick if you need it (see at the end).

### Network setup
***

I've came up with the following setup for Orange:

!["Orange Setup](http://community.ubnt.com/t5/image/serverpage/image-id/25010i86619C9E81B7169F/image-size/original?v=mpbl-1&px=-1 "Orange Setup")



### Edgemax setup
***

This is how we gonna set up the edgemax (based on the 3 ports).

  * 2 separated LANs on ports eth0 and eth2
  * Fully functionnal LANs with DHCP
  * LANs are isolated (static routes could link them).

On the software side :

  * Simple Stupid Setup.
  * No firewall, you'll manage this :)
  * Upnp activated on both LANs
  * Debian sources setup

!["Edgemax"](https://www.evernote.com/shard/s1/sh/54cd76a2-d198-4e8b-9a35-f8003a77301e/c51966d58cc33ca732cf2b4d599dfc87/deep/0/edgerouter-lite-1.jpg)

### Requirements
***

You'll need :

  * Minimal general handling of the edgemax, CLI, editing conf, etc...
  * 1.5 beta firmware (at the moment of writing beta1 is the latest)
  * Mac address of your original Livebox provided by Orange (written on the back of the Livebox)

All files used for this tutorial are available in the github repo.

### Get your TV mac Address
***

You TV mac address is the same than your livebox one with "4" added to the
last byte.

So if you have a mac adress like : a1:b2:c3:d4:e5:f6

f6 + 4 = fa

Your TV mac address will be a1:b2:c3:d4:e5:fa

### Edgemax Setup
***

I presume you are root, by doing :

  * `sudo -i` or prefixing commands with `sudo`

Prepare and load the conf and assets.

  * Download [https://github.com/c0mm0n/edgemax4orange/blob/master/config-orange.boot](https://github.com/c0mm0n/edgemax4orange/blob/master/config-orange.boot)
  * Edit lines 44 and 45 with your Orange logins (fti/xxxxx and password)
  * Upload file on edgemax in /config folder


  * Download [https://github.com/c0mm0n/edgemax4orange/blob/master/dhclient.conf](https://github.com/c0mm0n/edgemax4orange/blob/master/dhclient.conf)
  * Open in a text editor
  * Replace the xx:xx:xx:xx:xx:xx with the TV mac address deduced from the livebox one (the +4 one). the "1:" must stay.
  * Upload file on edgemax in /config folder


  * Download [https://github.com/c0mm0n/edgemax4orange/blob/master/tv.sh](https://github.com/c0mm0n/edgemax4orange/blob/master/tv.sh)
  * Upload file on edgemax in /config folder


Now you should be able to load the conf in ssh :

`configure`
`load config-orange.boot`
`commit`

If everything went ok, you can `save` otherwise `discard` and start again,
something's wrong.

Now it's time to plug ONT on port eth1, your LAN on eth0 or eth2, and reboot
the edgemax.

If everything went ok, you now have Internet access through the edgemax.

Now for the TV services

  * Install vlan : either `apt-get install vlan` (untested but should be the easiest method)
  * or get it there : [https://github.com/c0mm0n/edgemax4orange/blob/master/vlan_1.9-3_mips.deb](https://github.com/c0mm0n/edgemax4orange/blob/master/vlan_1.9-3_mips.deb) and install with `dpkg -i /config/scripts/post-config.d/vlan_1.9-3_mips.deb`

Then, in ssh :

`modprobe 8021q`


Now is time to launch my custom script which will :

  1. Set priorities on Vlans
  2. Launch DHCP on br0
  3. Launch igmpproxy

`sh /config/tv.sh`

Now you should :

  1. Have internet access
  2. Got an IP through dhcp on br0

Check like this for the IP on br0 `show interfaces`

!["interfaces"](https://www.evernote.com/shard/s1/sh/e6e3c4ab-15b5-43a4-ac30-2e29316400c3/8df6f38e2c8807482d576f56658f641c/deep/0/jb---ssh---80-24-et-jb---ssh---239-73.png)

One last thing for replays/vod, static routes.

  * Get the IP you got from the dhcp on br0
  * Let's say this IP is 10.54.56.154
  * Change the last part with "254", we now have : 10.54.56.254

Use this gateway to setup your static routes like this 

`route add -net 80.10.117.120/31 gateway 10.54.56.254`
`route add -net 81.253.206.0/24 gateway 10.54.56.254`
`route add -net 81.253.210.0/23 gateway 10.54.56.254`
`route add -net 81.253.214.0/23 gateway 10.54.56.254`
`route add -net 172.19.20.0/23 gateway 10.54.56.254`
`route add -net 172.20.224.167/32 gateway 10.54.56.254`
`route add -net 172.23.12.0/22 gateway 10.54.56.254`
`route add -net 193.253.67.88/29 gateway 10.54.56.254`
`route add -net 193.253.153.227/32 gateway 10.54.56.254`
`route add -net 193.253.153.228/32 gateway 10.54.56.254`

**You must replace 10.54.56.254 with the value you deduced base on the DHCP IP of br0 and type this through ssh on the edgemax.**

**Now reboot your Orange TV Box, everything should work fine.**

**If everything is OK :**

**Copy / Move the "tv.sh" script in /config/scripts/post-config.d/ for automatic startup execution.**

**You'll probably need to (unsure, check the script rights)**

  * **chmod +x tv.sh**


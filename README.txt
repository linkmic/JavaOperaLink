=====================================================
 Link - Opera Link Java Web application demo
=====================================================

This is a simple sample java web application that will retrieve
a user's SpeedDials and show them as a web page.

The instructions below assume that you are familiar with working
in a J2EE environment that used Apache Tomcat and Maven, on an
Ubuntu (or Debian) Linux machine.

License
=======
The source code included in this distribution is distributed under the
BSD license:

Copyright © 2010, Opera Software
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

* Redistributions of source code must retain the above copyright
  notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.
* Neither the name of Opera Software nor the names of its contributors
  may be used to endorse or promote products derived from this
  software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Compilation dependencies
========================
This project depends on the Opera Link client library found here.
http://github.com/operasoftware/JavaOperaLinkClient

Instructions
============

To build the webapp you need a working Maven installation. 

A quick-start guide for Ubuntu would be:
$> sudo apt-get install sun-java6-jre
$> sudo apt-get install maven
$> sudo apt-get install tomcat6

Grab the source code for this example

$> cd ~ 
$> git clone git://github.com/operasoftware/JavaOperaLink/

Grab the opera link Java client and build it
$> cd ~ 
$> git clone git://github.com/operasoftware/JavaOperaLinkClient
$> cd JavaOperaLink
$> mvn install

This will result in a the opera-link-client.jar file being deployed
into the .m2 folder on your local machine

Configure the webapp OAuth details in the opera.properties file:
You can use the default key and secret.
opera.consumerKey: SlKQfV4W3huPftrFxPbAHmvCJTZ04sIr
opera.consumerSecret: gFgcy8USFfI3LV8qF3e96bTyYqz4LRqS

And set the callback URL to match the server that you will deploy the example
on. In this case, the local machine.
opera.callbackUrl: http://127.0.0.1/Link/LinkServlet/

Alternativley, go to https://auth.opera.com/service/oauth and register
your own application and use your own key and secret and callback url
of your liking.

Build the web application
$> cd ~/JavaOperaLink
$> mvn package -Dmaven.test.skip=true (there are no tests)

Much downloading and grinding will happen.
A file called Link.war should be produced in the "target" folder.

Deploy to tomcat and start tomcat
$> cp target/Link.war /var/lib/tomcat6/webapps
$> sudo /etc/init.d/tomcat6 restart

Visit
http://127.0.0.1:8080/Link
Profit $

Known issues
============
Works out of the box on Ubuntu 10.10. If you are running an older version,
you may need to edit the policies in /etc/tomcat6/policy.d to enable
the application to connect to https://auth.opera.com and 
https://link.api.opera.com





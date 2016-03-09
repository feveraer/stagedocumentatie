# bin/bash

echo "sudo apt-get -y install python-pip"
sudo apt-get -y install python-pip
echo "sudo pip install jupyter"
sudo pip install jupyter > /dev/null
echo "sudo pip install path.py"
sudo pip install path.py > /dev/null

echo "export IPYTHON=1"
echo "export IPYTHON=1" >> /etc/profile
export IPYTHON=1

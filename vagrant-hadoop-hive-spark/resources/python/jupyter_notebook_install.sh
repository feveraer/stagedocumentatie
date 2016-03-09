# bin/bash

#IPython install
echo "sudo apt-get -y install python-pip"
sudo apt-get -y install python-pip > /dev/null
echo "sudo pip install jupyter"
sudo pip install jupyter > /dev/null
echo "sudo pip install path.py"
sudo pip install path.py > /dev/null
echo "export IPYTHON=1"
echo "export IPYTHON=1" >> /etc/profile
export IPYTHON=1

#Jypyter Notebook install
echo "sudo pip install markupsafe"
sudo pip install markupsafe > /dev/null
echo "sudo apt-get -y install python-dev"
sudo apt-get -y install python-dev > /dev/null
echo "sudo pip install pyzmq"
sudo pip install pyzmq > /dev/null
echo "sudo pip install singledispatch"
sudo pip install singledispatch > /dev/null
echo "sudo pip install backports_abc"
sudo pip install backports_abc > /dev/null
echo "sudo pip install certifi"
sudo pip install certifi > /dev/null
echo "sudo pip install jsonschema"
sudo pip install jsonschema > /dev/null

cp -f /vagrant/resources/var_export/.bash_aliases /home/vagrant/
source ~/.bashrc

echo
echo "To run Spark with Jupyter notebook, execute:"
echo "  spark-notebook"

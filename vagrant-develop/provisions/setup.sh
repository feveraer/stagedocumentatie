#!/bin/bash
echo " ==================================="
echo "| Updating the source list ...      |"
echo " ==================================="
sudo apt-get -qq update

echo
echo " ==================================="
echo "| Installing Java 7 ...             |"
echo " ==================================="
sudo apt-get install -y default-jdk > /dev/null

echo
echo " ====================================="
echo "| Installing additional software ... |"
echo " ====================================="
wget --progress=bar:force "http://download.netbeans.org/netbeans/8.1/final/bundles/netbeans-8.1-javase-linux.sh"
chmod 755 netbeans-8.1-javase-linux.sh
echo "To install netbeans run:"
echo "  ./netbeans-8.1-javase-linux.sh"

#!/bin/bash

# any future command that fails will exit the script
set -e

ls
eval $(ssh-agent -s)
echo 'this is updateAndRestart develop'
ssh-add ~/.ssh/enforge-drn
rm -rf ddp-api
rm -rf dpp_designgen
rm -rf ddp-ui2
rm -rf admin_panel
rm -rf /public_html
# clone the repo again
git clone git@gitlab.com:ddprod/ddp-api.git
git clone git@gitlab.com:ddprod/dpp_designgen.git
git clone git@gitlab.com:ddprod/ddp-ui2.git
git clone git@gitlab.com:ddprod/admin_panel.git
cd ddp-api
echo 'copying develop files'
git checkout develop
git pull
#java-api
cp src/main/resources/applicationDev src/main/resources/application.properties
#python-api
cd ../dpp_designgen/flask_rest_api_v3
git checkout develop
git pull
cp config_develop.yml config.yml
#angular-ui
cd ../../ddp-ui2
git checkout develop
git pull
echo ls
cp -a dist/. ~/public_html/
cp src/environments/environment-develop.ts src/environments/environment.prod.ts
#angular admin-panel
cd ../admin_panel
git checkout develop
git pull
cp src/environments/environment-develop.ts src/environments/environment.ts
#end of copying
echo 'end of copying'

cd ../ddp-api
#run docker-compose
sudo docker-compose down
sudo docker-compose rm -f
#Remove all containers
sudo docker system prune -a -f
sudo docker-compose up --force-recreate --build -d

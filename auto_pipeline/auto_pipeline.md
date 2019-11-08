#Create auto dev ops for git application

###1. Create the deploy script 

###1.1 Create the disableHostKeyChecking script

###1.2 Create the updateAndRestart script to restart services on the server

##For deploying on gitlab: create a .gitlab-cy.yml configuration file in order to enable pipelines and auto deploy with each push on gitlab

###2. add ssh keys to the server and gitlab(or the machine you are deploying from, public key on the server)

###2.1 make sure authorized_keys(644) and .ssl(700) directory have correct permissions or it work work 
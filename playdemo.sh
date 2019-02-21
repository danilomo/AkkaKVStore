#!/bin/bash

cd deployment

echo "Starting frontend nodes"
ansible-playbook start_nodes.yml -e "role=frontend"
sleep 5

echo "Starting backend nodes"
ansible-playbook start_nodes.yml -e "role=backend"
sleep 5

echo "Starting senders and receivers"
ansible-playbook start_senders_and_receivers.yml

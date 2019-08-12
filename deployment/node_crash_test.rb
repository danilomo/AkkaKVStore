#!/usr/bin/env ruby
#coding:utf-8

load './cluster_config.rb'

cluster = Cluster.new()

output = `ansible-playbook start_nodes.yml -e "role=frontend sbr_strategy=reasonable_downing"`
puts output

sleep 5

output = `ansible-playbook start_nodes.yml -e "role=backend sbr_strategy=reasonable_downing"`
puts output

sleep 5

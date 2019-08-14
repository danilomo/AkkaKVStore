#!/usr/bin/env ruby
#coding:utf-8

load './cluster_config.rb'; cluster = Cluster.new(frontends: 2, backends: 5)

cluster.split_network(["frontend_2", "backend_5"])

sleep 60

cluster.put_entry( "192.168.1.2", "myTable", "foo", "FOO_FOO" )
cluster.put_entry( "192.168.1.3", "myTable", "foo", "BAR_BAR" )

val1 = cluster.get_entry( "192.168.1.2", "myTable", "foo" )
val2 = cluster.get_entry( "192.168.1.3", "myTable", "foo" )

puts "myTable@192.168.1.2['foo'] = #{val1}"
puts "myTable@192.168.1.3['foo'] = #{val2}"

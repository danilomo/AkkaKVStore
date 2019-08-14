#!/usr/bin/env ruby
#coding:utf-8

load './cluster_config.rb'


def first_node_not_empty_shardregions(cluster, not_equals_to: "")
  cluster
    .list_of_nodes
    .map { |n| n[1] }
    .detect { |host| (host != not_equals_to) and (not cluster.get_shard_regions(host).empty?) }
end

cluster = Cluster.new(frontends: 2, backends: 5)

puts "Putting some value on the key-value storage..."
cluster.put_entry( "192.168.1.2", "myTable", "foo", "BAR" )

puts "Getting value from key-value storage:"
value = cluster.get_entry( "192.168.1.2", "myTable", "foo" )
puts "\tThe value we just sent: #{value}"

puts "Let's find out where the stored value is:"
address = first_node_not_empty_shardregions(cluster)
puts "\tThe value we sent is stored on: #{address}"

puts "Let's freeze this node for 30 seconds and try to access the same table..."
cluster.freeze_node(cluster.host_name(address), 30)

puts "Putting value on key-value storage again..."
cluster.put_entry( "192.168.1.2", "myTable", "foo", "FOO FOO" )

puts "Getting value from key-value storage again:"
value = cluster.get_entry( "192.168.1.2", "myTable", "foo" )
puts "\tThe value we just sent: #{value}"

puts "Let's find out where the stored value is after resharding:"
new_address = first_node_not_empty_shardregions(cluster, not_equals_to: address)
puts "The value we sent is stored on: #{new_address}"

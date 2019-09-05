#!/usr/bin/env ruby
#coding:utf-8

load './cluster_config.rb'


def first_node_not_empty_shardregions(cluster, not_equals_to: [])
  cluster
    .list_of_nodes
    .map { |n| n[1] }
    .detect { |host| (not not_equals_to.include?(host)) and (not cluster.get_shard_regions(host).empty?) }
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

puts "Let's partition the network (separate the backend node and the first frontend from the rest)..."
cluster.split_network([cluster.host_name(address), "frontend_1"])

puts "Sleeping for 40 seconds..."
sleep 40

#puts "Putting value on key-value storage again (via second frontend)..."
#cluster.put_entry( "192.168.1.3", "myTable", "foo", "FOO FOO" )

#puts "Getting value from key-value storage again:"
#value = cluster.get_entry( "192.168.1.3", "myTable", "foo" )
#puts "\tThe value we just sent: #{value}"

#puts "Let's find out where the stored value is after resharding:"
#new_address = first_node_not_empty_shardregions(cluster, not_equals_to: [address, "192.168.1.2"])
#puts "The value we sent is stored on: #{new_address}"

puts "Cluster members: #{cluster.get_cluster_members('192.168.1.3')}"

# restoring the network
cluster.merge_network([cluster.host_name(address), "frontend_1"])


require 'set'
require 'net/http'
require 'json'

NO_CONFIG = Proc.new { |b| nil }

class Cluster  
  def initialize(
        base_ip: "192.168.1",
        senders: 0,
        receivers: 0,
        frontends: 2,
        backends: 2,
        specific_config: {}
      )
    
    @base_ip = base_ip
    @senders   = senders
    @receivers = receivers
    @frontends = frontends
    @backends  = backends    
    @seed_nodes = ((1..@frontends).map { |n| "\"akka.tcp://kvstore@#{@base_ip}.#{n + 1}:2551\"" }).join(", ")
    @groups = { "backend" => [], "frontend" => [], "sender" => [], "receiver" => [] }
    @specific_config = specific_config
    @list_of_nodes = get_list_of_nodes
  end

  attr_reader :seed_nodes
  attr_reader :groups
  attr_reader :list_of_nodes
  
  def apply_specific_config(name, box)
    block = @specific_config.fetch(name, NO_CONFIG)
    block.call(box)
  end

  def configure_virtualbox_vm(vb,name)
    vb.name = name
    vb.memory = 512
    vb.cpus = 1
  end

  def get_list_of_nodes
    indexes      = (1..(@frontends + @backends + @senders + @receivers)).to_a  
    sufixes      = [ (1 .. @frontends).to_a, (1 .. @backends).to_a, (1 .. @senders).to_a, (1 .. @receivers).to_a ].flatten
    roles        = [ ["frontend"] * @frontends, ["backend"] * @backends, ["sender"] * @senders, ["receiver"] * @receivers ].flatten
    names        = roles.zip(sufixes).map { |item| "#{item[0]}_#{item[1]}" }
    ip_addresses = indexes.map { |i| "#{@base_ip}.#{1 + i}" }
    last_node    = indexes.map { |i| i == indexes.length }

    names.zip( ip_addresses, roles, last_node )
  end

  def split_network(nodes)
    alter_network(nodes,"disconnect_from")
  end

  def merge_network(nodes)
    alter_network(nodes,"connect_to")
  end  
  
  def alter_network(nodes, action)
    node_set = nodes.to_set
    other_partition = list_of_nodes()
                        .select { |node| ! node_set.member?(node[0]) }
                        .map { |node| node[1] }
                        .join(" ")

    nodes.each { |src|
      `vagrant ssh #{src} -c 'sudo #{action} #{other_partition}'`
    }
  end

  def crash_node(node)
    `vagrant ssh #{node} -c 'sudo kill -9 $(pgrep java)'`    
  end

  def freeze_node(node, time)
    `vagrant ssh #{node} -c 'sudo pkill -STOP java && sleep #{time} && sudo pkill -CONT java'`
  end

  def singleton_location(frontend_addr)
    JSON.parse(
      Net::HTTP.get(
        frontend_addr,
        "/singleton",
        8000
      )
    )["address"]
  end

  def leader_location(frontend_addr)
    str = JSON.parse(
      Net::HTTP.get(
        frontend_addr,
        "/cluster/members",
        8080
      )
    )["leader"]

    str
      .match('[^\@]+[\@]([^\:]+)[:]([1-9]+)')
      .captures[0]
  end

  def host_name(addr)
    list_of_nodes
      .find { |n| n[1] == addr }[0]
  end

  def address(host_name)
    list_of_nodes
      .find { |n| n[0] == host_name }[1]
  end

  def put_entry(host, table, key, value)
    req = Net::HTTP::Put.new("/kvstorage/#{table}/#{key}", initheader = { 'Content-Type' => 'application/json'})
    req.body = { :value => value }.to_json
    Net::HTTP.new(host, 8000).start { |http| http.request(req) }    
  end

  def get_entry(host, table, key)
    JSON.parse(
      Net::HTTP.get(
        host,
        "/kvstorage/#{table}/#{key}",
        8000
      )
    )["value"]
  end

  def get_shard_regions(host)
    JSON.parse(
      Net::HTTP.get(
        host,
        "/cluster/shards/storage",
        8080
      )
    )["regions"]        
  end

  def get_cluster_members(host)
    members = JSON.parse(
      Net::HTTP.get(
        host,
        "/cluster/members",
        8080
      )
    )["members"]

    members
      .map { |m| m["node"]
               .match('[^\@]+[\@]([^\:]+)[:]([1-9]+)')
               .captures[0]
    }
      .sort
  end

  private :alter_network, :get_list_of_nodes
end




require 'set'

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
  end

  attr_reader :seed_nodes
  attr_reader :groups
  
  def apply_specific_config(name, box)
    block = @specific_config.fetch(name, NO_CONFIG)
    block.call(box)
  end

  def configure_virtualbox_vm(vb,name)
    vb.name = name
    vb.memory = 512
    vb.cpus = 1
  end

  def list_of_nodes
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
  end

  def freeze_node(node)
  end

end

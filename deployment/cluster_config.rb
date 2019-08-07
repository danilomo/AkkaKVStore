class Cluster  

  @@NO_CONFIG = Proc.new { |b| nil }
  
  def initialize(
        base_ip: "192.168.1",
        senders: 0,
        receivers: 0,
        frontends: 2,
        backends: 2
      )
    
    @base_ip = base_ip
    @senders   = senders
    @receivers = receivers
    @frontends = frontends
    @backends  = backends
    
    @seed_nodes = ((1..@frontends).map { |n| "\"akka.tcp://kvstore@#{@base_ip}.#{n + 1}:2551\"" }).join(", ")
    @groups = { "backend" => [], "frontend" => [], "sender" => [], "receiver" => [] }

    @specific_config = {
    }
  end

  def seed_nodes()
    @seed_nodes
  end

  def groups()
    @groups
  end
  
  def apply_specific_config(name, box)
    block = @specific_config.fetch(name, @@NO_CONFIG)
    block.call(box)
  end

  def configure_virtualbox_vm(vb,name)
    vb.name = name
    vb.memory = 512
    vb.cpus = 1
  end

  def list_of_nodes()
    indexes      = (1..(@frontends + @backends + @senders + @receivers)).to_a  
    sufixes      = [ (1 .. @frontends).to_a, (1 .. @backends).to_a, (1 .. @senders).to_a, (1 .. @receivers).to_a ].flatten
    roles        = [ ["frontend"] * @frontends, ["backend"] * @backends, ["sender"] * @senders, ["receiver"] * @receivers ].flatten
    names        = roles.zip(sufixes).map { |item| "#{item[0]}_#{item[1]}" }
    ip_addresses = indexes.map { |i| "#{@base_ip}.#{1 + i}" }
    last_node    = indexes.map { |i| i == indexes.length }

    names.zip( ip_addresses, roles, last_node )
  end


end

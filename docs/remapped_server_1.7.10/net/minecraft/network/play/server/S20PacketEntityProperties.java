package net.minecraft.network.play.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S20PacketEntityProperties extends Packet {

    private int field_149445_a;
    private final List field_149444_b = new ArrayList();

    public S20PacketEntityProperties() {}

    public S20PacketEntityProperties(int i, Collection collection) {
        this.field_149445_a = i;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            IAttributeInstance iattributeinstance = (IAttributeInstance) iterator.next();

            this.field_149444_b.add(new S20PacketEntityProperties$Snapshot(this, iattributeinstance.func_111123_a().func_111108_a(), iattributeinstance.func_111125_b(), iattributeinstance.func_111122_c()));
        }

    }

    @Override
    public void func_148837_a(PacketBuffer packetbuffer) {
        this.field_149445_a = packetbuffer.readInt();
        int i = packetbuffer.readInt();

        for (int j = 0; j < i; ++j) {
            String s = packetbuffer.func_150789_c(64);
            double d0 = packetbuffer.readDouble();
            ArrayList arraylist = new ArrayList();
            short short0 = packetbuffer.readShort();

            for (int k = 0; k < short0; ++k) {
                UUID uuid = new UUID(packetbuffer.readLong(), packetbuffer.readLong());

                arraylist.add(new AttributeModifier(uuid, "Unknown synced attribute modifier", packetbuffer.readDouble(), packetbuffer.readByte()));
            }

            this.field_149444_b.add(new S20PacketEntityProperties$Snapshot(this, s, d0, arraylist));
        }

    }

    @Override
    public void func_148840_b(PacketBuffer packetbuffer) {
        packetbuffer.writeInt(this.field_149445_a);
        packetbuffer.writeInt(this.field_149444_b.size());
        Iterator iterator = this.field_149444_b.iterator();

        while (iterator.hasNext()) {
            S20PacketEntityProperties$Snapshot s20packetentityproperties$snapshot = (S20PacketEntityProperties$Snapshot) iterator.next();

            packetbuffer.func_150785_a(s20packetentityproperties$snapshot.func_151409_a());
            packetbuffer.writeDouble(s20packetentityproperties$snapshot.func_151410_b());
            packetbuffer.writeShort(s20packetentityproperties$snapshot.func_151408_c().size());
            Iterator iterator1 = s20packetentityproperties$snapshot.func_151408_c().iterator();

            while (iterator1.hasNext()) {
                AttributeModifier attributemodifier = (AttributeModifier) iterator1.next();

                packetbuffer.writeLong(attributemodifier.func_111167_a().getMostSignificantBits());
                packetbuffer.writeLong(attributemodifier.func_111167_a().getLeastSignificantBits());
                packetbuffer.writeDouble(attributemodifier.func_111164_d());
                packetbuffer.writeByte(attributemodifier.func_111169_c());
            }
        }

    }

    public void func_148833_a(INetHandlerPlayClient inethandlerplayclient) {
        inethandlerplayclient.func_147290_a(this);
    }
}

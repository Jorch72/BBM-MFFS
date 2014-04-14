package resonantinduction.archaic.crate;

import java.util.HashSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import resonantinduction.core.ResonantInduction;
import resonantinduction.core.prefab.imprint.ItemImprint;
import calclavia.api.resonantinduction.IFilterable;
import calclavia.lib.network.IPacketReceiver;
import calclavia.lib.network.PacketHandler;
import calclavia.lib.prefab.tile.TileExternalInventory;
import calclavia.lib.utility.inventory.IExtendedStorage;

import com.google.common.io.ByteArrayDataInput;

/** Basic single stack inventory.
 * 
 * TODO: Add filter-locking feature. Put filter in, locks the crate to only use that item.
 * 
 * @author DarkGuardsman */
public class TileCrate extends TileExternalInventory implements IPacketReceiver, IExtendedStorage, IFilterable
{
    /** max meta size of the crate */
    public static final int maxSize = 2;

    /** delay from last click */
    public long prevClickTime = -1000;

    /** Check to see if oreName items can be force stacked */
    public boolean oreFilterEnabled = false;

    /** Collective total stack of all inv slots */
    private ItemStack sampleStack;
    private ItemStack filterStack;

    @Override
    public InventoryCrate getInventory()
    {
        if (this.inventory == null)
        {
            inventory = new InventoryCrate(this);
        }
        return (InventoryCrate) this.inventory;
    }

    /** Gets the sample stack that represent the total inventory */
    public ItemStack getSampleStack()
    {
        if (this.sampleStack == null)
        {
            this.buildSampleStack();
        }
        return this.sampleStack;
    }

    /** Builds the sample stack using the inventory as a point of reference. Assumes all items match
     * each other, and only takes into account stack sizes */
    public void buildSampleStack()
    {
        ItemStack newSampleStack = null;
        boolean rebuildBase = false;

        /* Creates the sample stack that is used as a collective itemstack */
        for (int slot = 0; slot < this.getSizeInventory(); slot++)
        {
            ItemStack slotStack = this.getInventory().getContainedItems()[slot];
            if (slotStack != null && Item.itemsList[slotStack.itemID] != null && slotStack.stackSize > 0)
            {
                if (newSampleStack == null)
                    newSampleStack = slotStack.copy();
                else
                    newSampleStack.stackSize += slotStack.stackSize;

                if (slotStack.stackSize > slotStack.getMaxStackSize())
                    rebuildBase = true;
            }
        }
        if (newSampleStack == null || newSampleStack.itemID == 0 || newSampleStack.stackSize <= 0)
            this.sampleStack = this.getFilter() != null ? this.getFilter().copy() : null;
        else
            this.sampleStack = newSampleStack.copy();

        /* Rebuild inventory if the inventory is not valid */
        if (this.sampleStack != null && (rebuildBase || this.getInventory().getContainedItems().length > this.getSizeInventory()))
        {
            this.getInventory().buildInventory(this.sampleStack);
        }
    }

    @Override
    public ItemStack addStackToStorage(ItemStack stack)
    {
        return BlockCrate.addStackToCrate(this, stack);
    }

    /** Adds an item to the stack */
    public void addToStack(ItemStack stack, int amount)
    {
        if (stack != null)
        {
            ItemStack newStack = stack.copy();
            newStack.stackSize = amount;
            this.addToStack(newStack);
        }
    }

    /** Adds the stack to the sample stack */
    public void addToStack(ItemStack stack)
    {
        if (stack != null && stack.stackSize > 0)
        {
            if (this.getSampleStack() == null)
            {
                this.sampleStack = stack;
                getInventory().buildInventory(getSampleStack());
            }
            else if (this.getSampleStack().isItemEqual(stack) || (this.oreFilterEnabled && OreDictionary.getOreID(getSampleStack()) == OreDictionary.getOreID(stack)))
            {
                getSampleStack().stackSize += stack.stackSize;
                getInventory().buildInventory(getSampleStack());
            }
        }
    }

    @Override
    public void onInventoryChanged()
    {
        super.onInventoryChanged();

        if (worldObj != null && !worldObj.isRemote)
        {
            PacketHandler.sendPacketToClients(getDescriptionPacket(), this.worldObj);
        }
    }

    @Override
    public boolean canStore(ItemStack stack, int slot, ForgeDirection side)
    {
        return getSampleStack() == null || stack != null && (stack.isItemEqual(getSampleStack()) || (this.oreFilterEnabled && OreDictionary.getOreID(getSampleStack()) == OreDictionary.getOreID(stack)));
    }

    /** Gets the current slot count for the crate */
    public int getSlotCount()
    {
        if (this.worldObj == null)
        {
            return TileCrate.getSlotCount(TileCrate.maxSize);
        }
        return TileCrate.getSlotCount(this.getBlockMetadata());
    }

    /** Gets the slot count for the crate meta */
    public static int getSlotCount(int metadata)
    {
        if (metadata >= 2)
        {
            return 256;
        }
        else if (metadata >= 1)
        {
            return 64;
        }
        return 32;
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    @Override
    public void onReceivePacket(ByteArrayDataInput data, EntityPlayer player, Object... extra)
    {
        if (this.worldObj.isRemote)
        {
            try
            {
                if (data.readBoolean())
                {
                    this.sampleStack = ItemStack.loadItemStackFromNBT(PacketHandler.readNBTTagCompound(data));
                    this.sampleStack.stackSize = data.readInt();
                }
                else
                {
                    this.sampleStack = null;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        this.buildSampleStack();
        ItemStack stack = this.getSampleStack();
        if (stack != null)
        {
            return ResonantInduction.PACKET_TILE.getPacket(this, true, stack.writeToNBT(new NBTTagCompound()), stack.stackSize);
        }
        else
        {
            return ResonantInduction.PACKET_TILE.getPacket(this, false);
        }
    }

    /** NBT Data */
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        /* Load current two inv methods */
        ItemStack stack = null;
        int count = nbt.getInteger("Count");
        if (nbt.hasKey("itemID"))
        {
            stack = new ItemStack(nbt.getInteger("itemID"), count, nbt.getInteger("itemMeta"));
        }
        else
        {
            stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("stack"));
            if (stack != null)
            {
                stack.stackSize = count;
            }
        }

        /* Only load sample stack if the read stack is valid */
        if (stack != null && stack.itemID != 0 && stack.stackSize > 0)
        {
            this.sampleStack = stack;
            this.getInventory().buildInventory(this.sampleStack);
        }
        this.oreFilterEnabled = nbt.getBoolean("oreFilter");

    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        /* Re-Build sample stack for saving */
        this.buildSampleStack();
        ItemStack stack = this.getSampleStack();
        /* Save sample stack */
        if (stack != null)
        {
            nbt.setInteger("Count", stack.stackSize);
            nbt.setCompoundTag("stack", stack.writeToNBT(new NBTTagCompound()));
        }
        nbt.setBoolean("oreFilter", this.oreFilterEnabled);
    }

    @Override
    public void setFilter(ItemStack filter)
    {
        this.filterStack = filter;
        this.onInventoryChanged();
    }

    @Override
    public ItemStack getFilter()
    {
        return this.filterStack;
    }

}

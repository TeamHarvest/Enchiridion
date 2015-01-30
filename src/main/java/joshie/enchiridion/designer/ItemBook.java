package joshie.enchiridion.designer;

import static joshie.enchiridion.designer.BookRegistry.getData;
import static joshie.enchiridion.designer.BookRegistry.getID;

import java.util.List;

import joshie.enchiridion.EConfig;
import joshie.enchiridion.EInfo;
import joshie.enchiridion.ETranslate;
import joshie.enchiridion.Enchiridion;
import joshie.enchiridion.designer.BookRegistry.BookData;
import joshie.enchiridion.helpers.ClientHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBook extends Item {
    private IIcon cover;
    private IIcon pages;
    public static final int NORMAL_BOOK = 0;

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        BookData data = getData(stack);
        return data == null ? super.getItemStackDisplayName(stack) : getDisplayName(data);
    }

    public String getDisplayName(BookData data) {
        if (data.displayNames.containsKey(ClientHelper.getLang())) {
            return data.displayNames.get(ClientHelper.getLang());
        } else return data.displayNames.get("en_US");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean var) {
        if (EConfig.CAN_EDIT_BOOKS) {
            list.add(ETranslate.translate("edit.shift"));
            list.add(ETranslate.translate("edit.save"));
        }

        BookData data = getData(stack);
        if (data != null && data.information != null) {
            list.addAll(data.information);
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        String id = getID(stack);
        if (id != null) {
            if (EConfig.CAN_EDIT_BOOKS) {
                if (player.isSneaking()) {
                    player.openGui(Enchiridion.instance, EInfo.BOOKS_EDIT_ID, player.worldObj, 1, 0, 0);
                } else {
                    player.openGui(Enchiridion.instance, EInfo.BOOKS_VIEW_ID, player.worldObj, 1, 0, 0);
                }
            } else player.openGui(Enchiridion.instance, EInfo.BOOKS_VIEW_ID, player.worldObj, 1, 0, 0);
        }

        return stack;
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int pass) {
        BookData data = getData(stack);
        if (data != null) {
            if (hasPass1(data)) {
                if (pass == 0 && data.iconColorisePass1) { //If this is pass one, we should try to colourise
                    return data.iconColorPass1;
                } else if (hasPass2(data) && data.iconColorisePass2) { //At pass two if we have a second icon use it
                    return data.iconColorPass2;
                } else { //If we failed to have a second icon, use the original
                    return 16777215;
                }
            }
        }

        //If we fail to complete the IconPassing then we use the defualt
        if (pass == 0) {
            return data.color;
        } else return 16777215;
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public int getRenderPasses(int meta) {
        return 2;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (String identifier : BookRegistry.getIDs()) {
            ItemStack stack = new ItemStack(item);
            stack.setTagCompound(new NBTTagCompound());
            stack.stackTagCompound.setString("identifier", identifier);
            list.add(stack);
        }
    }

    private boolean hasPass1(BookData data) {
        return data.iconPass1 != null && !data.iconPass1.equals("");
    }

    private boolean hasPass2(BookData data) {
        return data.iconPass2 != null && !data.iconPass2.equals("");
    }

    public IIcon getIcon(String label) {
        return BookIconPatcher.map.getTextureExtry(label);
    }

    /** Worry about the icons later **/
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        BookData data = getData(stack);
        if (data != null) {
            if (hasPass1(data)) {
                if (pass == 0) { //At pass one, try to send back the first icon                    
                    IIcon icon = getIcon(data.iconPass1);
                    if (icon != null) return icon;
                } else if (hasPass2(data)) { //At pass two if we have a second icon use it                    
                    IIcon icon = getIcon(data.iconPass2);
                    if (icon != null) return icon;
                } else { //If we failed to have a second icon, use the original                    
                    IIcon icon = getIcon(data.iconPass1);
                    if (icon != null) return icon;
                }
            }
        }

        //If we fail to complete the IconPassing then we use the defualt
        if (pass == 0) {
            return cover;
        } else return pages;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        cover = iconRegister.registerIcon("enchiridion:cover");
        pages = iconRegister.registerIcon("enchiridion:pages");
    }
}

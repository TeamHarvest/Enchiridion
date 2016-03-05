package joshie.enchiridion.gui.book.features;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.book.IFeatureProvider;
import joshie.enchiridion.gui.book.GuiSimpleEditor;
import joshie.enchiridion.gui.book.GuiSimpleEditorItem;
import joshie.enchiridion.helpers.StackHelper;
import joshie.enchiridion.util.IItemSelectable;
import net.minecraft.item.ItemStack;

public class FeatureItem extends FeatureAbstract implements IItemSelectable {
	public String item;
	public transient float size;
	
	public FeatureItem(){}
	public FeatureItem(ItemStack item) {
		setItemStack(item);
	}
	
	public transient ItemStack stack;
	
	@Override
	public FeatureItem copy() {
	    return new FeatureItem(stack);
	}
	
	@Override
	public String getName() {
		return stack == null ? super.getName() : stack.getDisplayName();
	}
	
	@Override
	public void update(IFeatureProvider position) {
		double width = position.getWidth();
		position.setHeight(width);
        size = (float) (width / 16D);
	}
	
	@Override
    public void draw(int xPos, int yPos, double width, double height, boolean isMouseHovering) {
		if (stack == null && item != null) {
			stack = StackHelper.getStackFromString(item);
		} else EnchiridionAPI.draw.drawStack(stack, xPos, yPos, size);
	}
	
	@Override
	public void setItemStack(ItemStack stack) {
		this.stack = stack;
		this.item = StackHelper.getStringFromStack(stack);
	}
	
	@Override
    public boolean getAndSetEditMode() {
		GuiSimpleEditor.INSTANCE.setEditor(GuiSimpleEditorItem.INSTANCE.setItem(this));
		return false;
	}
}
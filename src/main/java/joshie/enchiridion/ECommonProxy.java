package joshie.enchiridion;

import static joshie.enchiridion.Enchiridion.instance;

import org.apache.logging.log4j.Level;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.book.IBookHandler;
import joshie.enchiridion.gui.GuiHandler;
import joshie.enchiridion.items.ItemBook;
import joshie.enchiridion.library.LibraryEvents;
import joshie.enchiridion.library.LibraryRegistry;
import joshie.enchiridion.library.handlers.EnchiridionBookHandler;
import joshie.enchiridion.library.handlers.RightClickBookHandler;
import joshie.enchiridion.library.handlers.TemporarySwitchHandler;
import joshie.enchiridion.library.handlers.WarpBookHandler;
import joshie.enchiridion.library.handlers.WriteableBookHandler;
import joshie.enchiridion.network.PacketHandleBook;
import joshie.enchiridion.network.PacketHandler;
import joshie.enchiridion.network.PacketLibraryCommand;
import joshie.enchiridion.network.PacketOpenLibrary;
import joshie.enchiridion.network.PacketSetLibraryBook;
import joshie.enchiridion.network.PacketSyncFile;
import joshie.enchiridion.network.PacketSyncLibraryAllowed;
import joshie.enchiridion.network.PacketSyncLibraryContents;
import joshie.enchiridion.network.PacketSyncMD5;
import joshie.enchiridion.util.ECreativeTab;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ECommonProxy {
    public static Item book;
    
    public void preInit() {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        book = new ItemBook().setCreativeTab(ECreativeTab.enchiridion).setHasSubtypes(true).setUnlocalizedName("book");
        EnchiridionAPI.instance = new EAPIHandler();
        EnchiridionAPI.library = new LibraryRegistry();
        EnchiridionAPI.library.registerBookHandler(new EnchiridionBookHandler()); //Enchiridion
        EnchiridionAPI.library.registerBookHandler(new WriteableBookHandler()); //Writeable Books
        EnchiridionAPI.library.registerBookHandler(new RightClickBookHandler()); //Default Handler
        EnchiridionAPI.library.registerBookHandler(new TemporarySwitchHandler()); //Switch Click Handler
        attemptToRegisterModdedBookHandler(WarpBookHandler.class);
        
        
        //Register events
        FMLCommonHandler.instance().bus().register(new LibraryEvents());
        MinecraftForge.EVENT_BUS.register(new LibraryEvents());
        
        //Register packets#
        PacketHandler.registerPacket(PacketSyncLibraryAllowed.class);
        PacketHandler.registerPacket(PacketLibraryCommand.class);
        PacketHandler.registerPacket(PacketSyncMD5.class);
        PacketHandler.registerPacket(PacketSyncFile.class);
        PacketHandler.registerPacket(PacketSyncLibraryContents.class, Side.CLIENT);
        PacketHandler.registerPacket(PacketOpenLibrary.class, Side.SERVER);
        PacketHandler.registerPacket(PacketHandleBook.class, Side.SERVER);
        PacketHandler.registerPacket(PacketSetLibraryBook.class, Side.SERVER);
        
        //Prepare the client for shizz
        setupClient();
    }
    
    public void attemptToRegisterModdedBookHandler(Class clazz) {
        try {
            Object o = clazz.newInstance(); //Let's try this!
            EnchiridionAPI.library.registerBookHandler((IBookHandler) o);
        } catch (Exception e) { Enchiridion.log(Level.INFO, "Enchiridion could not create an instance of " + clazz.getName() + " as the mods this handler relies on, are not supplied");}
    }

    public void setupClient() {}

	public void onConstruction() {}
}
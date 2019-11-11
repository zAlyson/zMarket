package br.alysonsantos.market.apis;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ActionBarAPI {
    
	private static Method a;
	private static Object typeMessage;
	private static Constructor<?> chatConstructor;
	
	public static void sendActionBar(Player player, String message) {
		try {
			Object chatMessage = a.invoke(null, "{\"text\":\"" + message + "\"}");
		    Object packet = chatConstructor.newInstance(chatMessage, typeMessage);
		    ReflectionUtils.sendPacket(player, packet);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public static void broadcastActionBar(String message) {
		try 
		{
			Object chatMessage = a.invoke(null, "{\"text\":\"" + message + "\"}");
		    Object packet = chatConstructor.newInstance(chatMessage, typeMessage);
		    for (Player player : Bukkit.getOnlinePlayers()) {
		    	ReflectionUtils.sendPacket(player, packet);
		    }
		} 
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	static void load() {
		try 
		{
			Class<?> typeMessageClass;
			Class<?> icbc = ReflectionUtils.getNMSClass("IChatBaseComponent");
			Class<?> ppoc = ReflectionUtils.getNMSClass("PacketPlayOutChat");
			
			if (icbc.getDeclaredClasses().length > 0) {
				a = icbc.getDeclaredClasses()[0].getMethod("a", String.class);
			} else {
				a = ReflectionUtils.getNMSClass("ChatSerializer").getMethod("a", String.class);
			}
			
				typeMessageClass = byte.class;
				typeMessage = (byte) 2;
			
			chatConstructor = ppoc.getConstructor(icbc,  typeMessageClass);	
		}
		catch (Throwable e) {}
	}
}
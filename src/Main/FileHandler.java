package Main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.bukkit.Bukkit;

import Maps.GameMap;
import Other.KillDeath;

public class FileHandler
{
	//Loads all maps from file
		@SuppressWarnings("unchecked")
		public static HashMap<String, GameMap> loadMaps(HashMap<String, GameMap> maps)
		{
			try
			{
				FileInputStream fileStream = new FileInputStream("plugins/Battleblox/maps.txt");
				ObjectInputStream objectStream = new ObjectInputStream(fileStream);
				maps = (HashMap<String, GameMap>) objectStream.readObject();
				fileStream.close();
				objectStream.close();
			}

			catch (ClassNotFoundException exception)
			{
				Bukkit.broadcastMessage(exception.getMessage());
			}
			catch (FileNotFoundException fileNotFound)
			{
				Bukkit.broadcastMessage(fileNotFound.getMessage());
			}
			catch (IOException IOerror)
			{
				Bukkit.broadcastMessage(IOerror.getMessage());
			}
			Bukkit.broadcastMessage(maps.size() + " maps loaded to memory.");
			return maps;
		}

		//Saves all current Maps to file
		public static void saveMaps(HashMap<String, GameMap> maps)
		{
			try
			{
				FileOutputStream fileStream = new FileOutputStream("plugins/Battleblox/maps.txt");
				ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
				objectStream.writeObject(maps);
				objectStream.close();
				fileStream.close();
			}
			catch (FileNotFoundException fileNotFound)
			{
				Bukkit.broadcastMessage(fileNotFound.getMessage());
			}
			catch (IOException IOerror)
			{
				Bukkit.broadcastMessage(IOerror.getMessage());
			}
			Bukkit.broadcastMessage(maps.size() + " maps saved from memory.");
		}
		
		
		//Loads all KDs from file
        @SuppressWarnings("unchecked")
        public static HashMap<String, KillDeath> loadKD(HashMap<String, KillDeath> kds)
        {
            try
            {
                FileInputStream fileStream = new FileInputStream("plugins/Battleblox/kd.txt");
                ObjectInputStream objectStream = new ObjectInputStream(fileStream);
                kds = (HashMap<String, KillDeath>) objectStream.readObject();
                fileStream.close();
                objectStream.close();
            }

            catch (ClassNotFoundException exception)
            {
                Bukkit.broadcastMessage(exception.getMessage());
            }
            catch (FileNotFoundException fileNotFound)
            {
                Bukkit.broadcastMessage(fileNotFound.getMessage());
            }
            catch (IOException IOerror)
            {
                Bukkit.broadcastMessage(IOerror.getMessage());
            }
            return kds;
        }

        //Saves all current KDs to file
        public static void saveKD(HashMap<String, KillDeath> kds)
        {
            try
            {
                FileOutputStream fileStream = new FileOutputStream("plugins/Battleblox/kd.txt");
                ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
                objectStream.writeObject(kds);
                objectStream.close();
                fileStream.close();
            }
            catch (FileNotFoundException fileNotFound)
            {
                Bukkit.broadcastMessage("File not found error");
            }
            catch (IOException IOerror)
            {
                Bukkit.broadcastMessage("IO Error");
            }
        }
}

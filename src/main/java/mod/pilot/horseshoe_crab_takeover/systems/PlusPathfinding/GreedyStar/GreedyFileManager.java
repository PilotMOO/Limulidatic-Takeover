package mod.pilot.horseshoe_crab_takeover.systems.PlusPathfinding.GreedyStar;

import net.minecraftforge.fml.loading.FMLPaths;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class GreedyFileManager {
    private static final String DATA_PATH_NAME = "greedy_star";
    private static final String TAG_DIRECTORY = "tags";
    private static final String FILE_CACHE_DIRECTORY = "world_cache";
    private enum GStarDirectory{
        TAGS(TAG_DIRECTORY, true),
        WORLD_CACHE(FILE_CACHE_DIRECTORY, false);

        GStarDirectory(String relativePath, boolean crashIfWeCantFuckingFindIt){
            this.relativePath = relativePath;
            this.crash = crashIfWeCantFuckingFindIt;
        }
        private final String relativePath;
        public final boolean crash;

        public Path defaultPath(){return path(dataPath);}
        public Path path(Path base){return base.resolve(relativePath);}
    }

    public static void setupGreedyTags() {
        Path path = FileSystems.getDefault().getPath(DATA_PATH_NAME);
        dataPath = FMLPaths.getOrCreateGameRelativePath(path);

        try {
            tags = getOrCreateDirectory(GStarDirectory.TAGS.defaultPath());
        } catch (IOException e) {
            if (GStarDirectory.TAGS.crash) throw new RuntimeException(e);
            else printFileCouldNotBeFoundOrCreated(GStarDirectory.TAGS, e);
        }
        try (Stream<Path> files = Files.list(tags)){
            files.forEach(GreedyFileManager::readJSONTagFile);
        } catch (Exception e) { throw new RuntimeException(e); }

        try {
            worldCache = getOrCreateDirectory(GStarDirectory.WORLD_CACHE.defaultPath());
        } catch (IOException e) {
            if (GStarDirectory.WORLD_CACHE.crash) throw new RuntimeException(e);
            else printFileCouldNotBeFoundOrCreated(GStarDirectory.WORLD_CACHE, e);
        }
    }



    private static Path getOrCreateDirectory(Path path) throws IOException {
        if (Files.notExists(path)){
            return Files.createDirectory(path);
        } else return path;
    }
    public static Path dataPath;

    public static Path tags;
    public static Path worldCache;

    private static void readJSONTagFile(Path path){
        try(FileReader fReader = new FileReader(path.toFile())){
            JsonReader jReader = Json.createReader(fReader);
            JsonObject jObject = jReader.readObject();

            //ToDo: Properly set up JSON-to-Tag parsing,
            // also set up parsing GreedyWorlds from JSONS.
            // add bidirectional tag assignment
            // (Allow both the GreedyWorld.json to assign tags to itself
            // and for tags to assign themselves to other GreedyWorlds via string identifiers)
        } catch (IOException ignored) {
            System.err.println("[GREEDY-STAR FILE MANAGER] FAILED to parse json file "
                    + path + ", could not compute tag");
        }
    }
    private static WorldTag[] allTags;

    public record WorldTag(String tag){
        public boolean fuzzy(WorldTag tag){return fuzzy(tag.tag);}
        public boolean fuzzy(String tag){return this.tag.equals(tag);}
    }

    private static void printFileCouldNotBeFoundOrCreated(GStarDirectory directory, IOException e){
        System.err.println("[GREEDY-STAR FILE MANAGER] FAILED to locate or create Path for Tag directory["
                + directory.defaultPath() + "], resulting in an error. This directory isn't flagged for crashing when accessing fails. Exception: " + e);
    }
}

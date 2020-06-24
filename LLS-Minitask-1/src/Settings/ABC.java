package Settings;

import java.util.LinkedList;
import java.util.List;

public class ABC {
	/**
	 * Used to supply paths to abc executables.
	 * @return List of Paths
	 */
	public static List<String> getABCExecutables(){
		List<String> paths = new LinkedList<String>();
		paths.add("/home/lukas/Programme/abc/build/abc");
		paths.add("C:/Program Files/ABC/abc10216");
		paths.add("S:/Programme/ABC-Tool/abc10216.exe");
		return paths;
	};

}

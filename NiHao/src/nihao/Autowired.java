package nihao;

/**
 * Link:<br>
 * · interfaces with implementations<br>
 * · abstract class with instances of non abstract descendant<br>
 * · classes with instances of class<br>
 * 
 * @author XWolf
 * 
 */
public @interface Autowired {
	/**
	 * 
	 * If set uses this implementation as wire
	 * 
	 */
	Class<?> use() default Void.class;

	/**
	 * 
	 * Wire descendant on first use
	 * 
	 * @deprecated future use
	 */
	boolean lazy() default false;
}

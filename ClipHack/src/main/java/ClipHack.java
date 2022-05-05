import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.function.Function;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 4/28/22
 * <p>Time: 12:12 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"HardCodedStringLiteral", "UseOfSystemOutOrSystemErr", "IteratorNextCanNotThrowNoSuchElementException"})
public final class ClipHack extends JPanel {

  private ComboBoxModel<FlavorData> flavorModel;
//  private List<Runnable> flavorChangeListeners = new LinkedList<>();
  Observable<FlavorData[]> clipboardObservable = new Observable<FlavorData[]>();
  
  private final JPanel topPanel;

  private FlavorData[] flavorDataModel;
  private JList<FlavorData> flavorList;

  public static void main(String[] args) {
    JFrame frame = new JFrame("ClipHack");
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setLocationByPlatform(true);
    frame.add(new ClipHack());
    frame.pack();
    frame.setVisible(true);
  }
  
  private static final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
  
  private final JComponent flavorPanel;
  
  private ClipHack() {
    super(new BorderLayout());
    topPanel = new JPanel(new FlowLayout());
    flavorPanel = makeFlavorPanel(new FlavorData[0]);
    add(BorderLayout.LINE_START, flavorPanel);
    add(BorderLayout.PAGE_START, topPanel);
    
    FlavorListener fl = e -> loadFlavors();
    clipboard.addFlavorListener(fl);
    loadFlavors();
  }
  
  private void loadFlavors() {
    System.out.println("---");
    DataFlavor[] flavors = clipboard.getAvailableDataFlavors();
    flavorDataModel = new FlavorData[flavors.length];
    int index = 0;
    for (DataFlavor flavor: flavors) {
      flavorDataModel[index++] = new FlavorData(flavor);
    }
    Arrays.sort(flavorDataModel);
    for (FlavorData data: flavorDataModel) {
      load(data.getTheFlavor());
    }
    
    installFlavorData(flavorDataModel);
    // JList
    clipboardObservable.hasChanged();
  }
  
  // This was written for a JPanel with a GridLayout, but it needs to work with a JList instead.
  private void load(DataFlavor flavor) {
    String fullType = String.format("%s/%s",flavor.getPrimaryType(), flavor.getSubType());
    String misMatch = fullType.equals(flavor.getHumanPresentableName()) ? "" : "Mismatch";
    final Class<?> representationClass = flavor.getRepresentationClass();
    String rCName = representationClass.getName();
    if (representationClass.isArray()) {
      rCName = String.format("\"%s\"", rCName);
    }
    String className = String.format("class=%s", rCName);
    String classMisMatch = flavor.getMimeType().contains(className) ? "" : ("Class Mismatch: " + representationClass);

    //noinspection UseOfSystemOutOrSystemErr
    System.out.printf("%-40s %8s -- mt: %-80s, %40s, %-30s %40s%n",  // NON-NLS
        flavor.getHumanPresentableName(),
        misMatch,
        flavor.getMimeType(),
        fullType,
        representationClass.getSimpleName(),
        classMisMatch);
  }
  
  private JComponent makeFlavorPanel() {
    flavorList = new JList<>();
    flavorList.setCellRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isCellSelected, final boolean cellHasFocus) {
        return super.getListCellRendererComponent(list, ((FlavorData)value).getMimeType(), index, isCellSelected, cellHasFocus);
      }
    });
    flavorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    flavorList.addListSelectionListener(e -> { selectedFlavorSet(flavorList.getSelectedValue()); });
    return flavorList;
  }
  
  private void installFlavorData(FlavorData[] data) {
    DefaultListModel<FlavorData> listModel = new DefaultListModel<>();
    for (FlavorData flavorData : data) {
      listModel.addElement(flavorData);
    }
    flavorList.getSelectionModel().setSelectionInterval(-1, -1);
    flavorList.setModel(listModel);
  }
  
  private void selectedFlavorSet(@Nullable FlavorData data) {
    if (data != null) {
      // todo: write me
    }
  }
  
  private class FlavorData implements Comparable<FlavorData> {
    private final DataFlavor theFlavor;
    private final String mimeType;
    private final String fullMimeType;
    private final Class<?> theClass;
    private final String className;
    private final String name;
    private final String charSet;
    private final Map<String, String> otherValues;
    private final MapKeyComparator mapKeyComparator;

    FlavorData(DataFlavor dataFlavor) {
      theFlavor = dataFlavor;
      fullMimeType = dataFlavor.getMimeType();
      mimeType = String.format("%s/%s", dataFlavor.getPrimaryType(), dataFlavor.getSubType());
      theClass = dataFlavor.getRepresentationClass();
      className = theClass.getName();
      name = dataFlavor.getHumanPresentableName();
      otherValues = makeRawMap(fullMimeType);
      charSet = otherValues.get("charset");
      otherValues.remove("charset");
      otherValues.remove("class");
      mapKeyComparator = new MapKeyComparator(otherValues);
    }

    public DataFlavor getTheFlavor() {
      return theFlavor;
    }

    public String getMimeType() {
      return mimeType;
    }

    public String getFullMimeType() {
      return fullMimeType;
    }

    public Class<?> getTheClass() {
      return theClass;
    }

    public String getClassName() {
      return className;
    }

    public String getName() {
      return name;
    }

    public String getCharSet() {
      return charSet;
    }

    public Map<String, String> getOtherValues() {
      return otherValues;
    }

    @Override
    public int compareTo(@NotNull final ClipHack.FlavorData that) {
      if (this.mimeType.equals(that.mimeType) && !this.name.equals(that.name)) {
        return this.name.equals(this.mimeType)? 1 : -1;
        
      }
      return Comparator.comparing(FlavorData::getMimeType)
          .thenComparing(FlavorData::getCharSet, Comparator.nullsFirst(Comparator.naturalOrder()))
          .thenComparing(FlavorData::getClassName)
          .thenComparing(mapKeyComparator)
          .compare(this, that);
    }

    @Override
    public boolean equals(final Object obj) {
      if (obj instanceof FlavorData) {
        FlavorData that = (FlavorData) obj;
        return Objects.equals(this.getMimeType(), that.getMimeType()) &&
            Objects.equals(this.getCharSet(), that.getCharSet()) &&
            Objects.equals(this.getClassName(), that.getClassName()) &&
            this.getOtherValues().equals(that.getOtherValues());
      }
      return false;
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.getMimeType(), this.getCharSet(), this.getClassName(), this.getOtherValues());
    }
  }

  private static String first(Map<String, String> map) {
    if (map.isEmpty()) {
      return "null";
    }
    return map.values().iterator().next();
  }

  private static class MapKeyComparator implements Comparator<FlavorData> {
    private final Comparator<String> nullFriendlyComparator = Comparator.nullsFirst(Comparator.naturalOrder());
    private final Map<String, String> map;
    MapKeyComparator(Map<String, String> map) {
      this.map = map;
    }

    @SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
    @Override
    public int compare(FlavorData one, FlavorData two) {
      final Map<String, String> o1 = one.otherValues;
      final Map<String, String> o2 = two.otherValues;
      //noinspection ObjectEquality
      if (o1 == o2) {
        return 0;
      }
      Set<String> done = new HashSet<>();
      for (String key: o1.keySet()) {
        done.add(key);
        int compared = nullFriendlyComparator.compare(o1.get(key), o2.get(key));
        if (compared != 0) {
          return compared;
        }
      }
      for (String key: o2.keySet()) {
        if (!done.contains(key)) {
          int compared = nullFriendlyComparator.compare(o1.get(key), o2.get(key));
          if (compared != 0) {
            return compared;
          }
        }
      }
      return 0;
    }
  }
  
  private Map<String, String> makeRawMap(String mime) {
    Map<String, String> rawMap = new TreeMap<>();
    StringTokenizer tokenizer = new StringTokenizer(mime, "; ");
    tokenizer.nextToken();
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      String[] keyValuePair = token.split("=");
      rawMap.put(keyValuePair[0], keyValuePair[1]);
    }
    return rawMap;
  }
  
  private void makeDisplayPanel(DataFlavor flavor) {
    JPanel displayPanel = new JPanel();
    add(BorderLayout.CENTER, displayPanel);
  }

  /**
   * ComboBoxModel Structure:
   * shortMimeType -> List of full mime-type
   * Full mime-type -> list of charSet
   * CharSet
   * @return
   */
  private JPanel fillTopPane(FlavorData flavorData) {
    JButton paste = new JButton("Refresh");
    paste.addActionListener(e -> loadFlavors());
    topPanel.add(paste);

    JComboBox<String> encodingChooser = new JComboBox<>();
//    flavorModel = encodingChooser.getModel();
    
    ListMap<String, FlavorData> mimeTypeMap = makeMap(flavorDataModel, FlavorData::getCharSet, f -> f);
    @SuppressWarnings("ConstantConditions")
    ComboBoxModel<String> model = new DefaultComboBoxModel<>((String[]) mimeTypeMap.keySet().toArray());
    encodingChooser.setModel(model);
    topPanel.add(encodingChooser);
  }
  
  private static <S, K extends Comparable<K>, V> ListMap<K, V> makeMap(
      S[] source, 
      Function<S, K> keyExtractor, 
      Function<S, V> valueExtractor
  ) {
    ListMap<K, V> listMap = new ListMap<>();
    for (S s: source) {
      listMap.addMapping(keyExtractor.apply(s), valueExtractor.apply(s));
    }
    return listMap;
  }

//  private static <K extends Comparable<K>, V> Map<K, V> makeMap(K[] source, Function<K, V> extractor) {
//    return Arrays
//        .stream(source)
//        .collect(Collectors.toMap(k->k, extractor));
////    Map<K, V> map = new TreeMap<>();
////    for (K key : source) {
////      map.put(key, extractor.apply(key));
////    }
////    return map;
//  }
//
//  private static <K extends Comparable<K>, V> Map<K, V> makeMap(Collection<K> source, Function<K, V> extractor) {
//    return source
//        .stream()
//        .collect(Collectors.toMap(k -> k, extractor, ));
////    Map<K, V> map = new TreeMap<>();
////    for (K key: source) {
////      map.put(key, extractor.apply(key));
////    }
////    return map;
//  }

  private static class ListMap<K, V> extends LinkedHashMap<K, List<V>> {
    public void addMapping(final K key, final V value) {
      if (!containsKey(key)) {
        super.put(key, new LinkedList<>());
      }
      get(key).add(value);
    }

    @Override
    public List<V> put(final K key, final List<V> value) {
      throw new IllegalArgumentException("Don't call put(), use addMapping()");
    }

    @Override
    public void putAll(final Map<? extends K, ? extends List<V>> map) {
      throw new IllegalArgumentException("Don't call put() or putAll(), use addMapping()");
    }
  }
}

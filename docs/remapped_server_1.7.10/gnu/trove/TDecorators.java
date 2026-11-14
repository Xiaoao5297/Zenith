package gnu.trove;

import gnu.trove.decorator.TByteByteMapDecorator;
import gnu.trove.decorator.TByteCharMapDecorator;
import gnu.trove.decorator.TByteDoubleMapDecorator;
import gnu.trove.decorator.TByteFloatMapDecorator;
import gnu.trove.decorator.TByteIntMapDecorator;
import gnu.trove.decorator.TByteListDecorator;
import gnu.trove.decorator.TByteLongMapDecorator;
import gnu.trove.decorator.TByteObjectMapDecorator;
import gnu.trove.decorator.TByteSetDecorator;
import gnu.trove.decorator.TByteShortMapDecorator;
import gnu.trove.decorator.TCharByteMapDecorator;
import gnu.trove.decorator.TCharCharMapDecorator;
import gnu.trove.decorator.TCharDoubleMapDecorator;
import gnu.trove.decorator.TCharFloatMapDecorator;
import gnu.trove.decorator.TCharIntMapDecorator;
import gnu.trove.decorator.TCharListDecorator;
import gnu.trove.decorator.TCharLongMapDecorator;
import gnu.trove.decorator.TCharObjectMapDecorator;
import gnu.trove.decorator.TCharSetDecorator;
import gnu.trove.decorator.TCharShortMapDecorator;
import gnu.trove.decorator.TDoubleByteMapDecorator;
import gnu.trove.decorator.TDoubleCharMapDecorator;
import gnu.trove.decorator.TDoubleDoubleMapDecorator;
import gnu.trove.decorator.TDoubleFloatMapDecorator;
import gnu.trove.decorator.TDoubleIntMapDecorator;
import gnu.trove.decorator.TDoubleListDecorator;
import gnu.trove.decorator.TDoubleLongMapDecorator;
import gnu.trove.decorator.TDoubleObjectMapDecorator;
import gnu.trove.decorator.TDoubleSetDecorator;
import gnu.trove.decorator.TDoubleShortMapDecorator;
import gnu.trove.decorator.TFloatByteMapDecorator;
import gnu.trove.decorator.TFloatCharMapDecorator;
import gnu.trove.decorator.TFloatDoubleMapDecorator;
import gnu.trove.decorator.TFloatFloatMapDecorator;
import gnu.trove.decorator.TFloatIntMapDecorator;
import gnu.trove.decorator.TFloatListDecorator;
import gnu.trove.decorator.TFloatLongMapDecorator;
import gnu.trove.decorator.TFloatObjectMapDecorator;
import gnu.trove.decorator.TFloatSetDecorator;
import gnu.trove.decorator.TFloatShortMapDecorator;
import gnu.trove.decorator.TIntByteMapDecorator;
import gnu.trove.decorator.TIntCharMapDecorator;
import gnu.trove.decorator.TIntDoubleMapDecorator;
import gnu.trove.decorator.TIntFloatMapDecorator;
import gnu.trove.decorator.TIntIntMapDecorator;
import gnu.trove.decorator.TIntListDecorator;
import gnu.trove.decorator.TIntLongMapDecorator;
import gnu.trove.decorator.TIntObjectMapDecorator;
import gnu.trove.decorator.TIntSetDecorator;
import gnu.trove.decorator.TIntShortMapDecorator;
import gnu.trove.decorator.TLongByteMapDecorator;
import gnu.trove.decorator.TLongCharMapDecorator;
import gnu.trove.decorator.TLongDoubleMapDecorator;
import gnu.trove.decorator.TLongFloatMapDecorator;
import gnu.trove.decorator.TLongIntMapDecorator;
import gnu.trove.decorator.TLongListDecorator;
import gnu.trove.decorator.TLongLongMapDecorator;
import gnu.trove.decorator.TLongObjectMapDecorator;
import gnu.trove.decorator.TLongSetDecorator;
import gnu.trove.decorator.TLongShortMapDecorator;
import gnu.trove.decorator.TObjectByteMapDecorator;
import gnu.trove.decorator.TObjectCharMapDecorator;
import gnu.trove.decorator.TObjectDoubleMapDecorator;
import gnu.trove.decorator.TObjectFloatMapDecorator;
import gnu.trove.decorator.TObjectIntMapDecorator;
import gnu.trove.decorator.TObjectLongMapDecorator;
import gnu.trove.decorator.TObjectShortMapDecorator;
import gnu.trove.decorator.TShortByteMapDecorator;
import gnu.trove.decorator.TShortCharMapDecorator;
import gnu.trove.decorator.TShortDoubleMapDecorator;
import gnu.trove.decorator.TShortFloatMapDecorator;
import gnu.trove.decorator.TShortIntMapDecorator;
import gnu.trove.decorator.TShortListDecorator;
import gnu.trove.decorator.TShortLongMapDecorator;
import gnu.trove.decorator.TShortObjectMapDecorator;
import gnu.trove.decorator.TShortSetDecorator;
import gnu.trove.decorator.TShortShortMapDecorator;
import gnu.trove.list.TByteList;
import gnu.trove.list.TCharList;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.TLongList;
import gnu.trove.list.TShortList;
import gnu.trove.map.TByteByteMap;
import gnu.trove.map.TByteCharMap;
import gnu.trove.map.TByteDoubleMap;
import gnu.trove.map.TByteFloatMap;
import gnu.trove.map.TByteIntMap;
import gnu.trove.map.TByteLongMap;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.map.TByteShortMap;
import gnu.trove.map.TCharByteMap;
import gnu.trove.map.TCharCharMap;
import gnu.trove.map.TCharDoubleMap;
import gnu.trove.map.TCharFloatMap;
import gnu.trove.map.TCharIntMap;
import gnu.trove.map.TCharLongMap;
import gnu.trove.map.TCharObjectMap;
import gnu.trove.map.TCharShortMap;
import gnu.trove.map.TDoubleByteMap;
import gnu.trove.map.TDoubleCharMap;
import gnu.trove.map.TDoubleDoubleMap;
import gnu.trove.map.TDoubleFloatMap;
import gnu.trove.map.TDoubleIntMap;
import gnu.trove.map.TDoubleLongMap;
import gnu.trove.map.TDoubleObjectMap;
import gnu.trove.map.TDoubleShortMap;
import gnu.trove.map.TFloatByteMap;
import gnu.trove.map.TFloatCharMap;
import gnu.trove.map.TFloatDoubleMap;
import gnu.trove.map.TFloatFloatMap;
import gnu.trove.map.TFloatIntMap;
import gnu.trove.map.TFloatLongMap;
import gnu.trove.map.TFloatObjectMap;
import gnu.trove.map.TFloatShortMap;
import gnu.trove.map.TIntByteMap;
import gnu.trove.map.TIntCharMap;
import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntLongMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TIntShortMap;
import gnu.trove.map.TLongByteMap;
import gnu.trove.map.TLongCharMap;
import gnu.trove.map.TLongDoubleMap;
import gnu.trove.map.TLongFloatMap;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TLongShortMap;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.map.TObjectCharMap;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.TObjectShortMap;
import gnu.trove.map.TShortByteMap;
import gnu.trove.map.TShortCharMap;
import gnu.trove.map.TShortDoubleMap;
import gnu.trove.map.TShortFloatMap;
import gnu.trove.map.TShortIntMap;
import gnu.trove.map.TShortLongMap;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.TShortShortMap;
import gnu.trove.set.TByteSet;
import gnu.trove.set.TCharSet;
import gnu.trove.set.TDoubleSet;
import gnu.trove.set.TFloatSet;
import gnu.trove.set.TIntSet;
import gnu.trove.set.TLongSet;
import gnu.trove.set.TShortSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TDecorators {

    private TDecorators() {}

    public static Map<Double, Double> wrap(TDoubleDoubleMap tdoubledoublemap) {
        return new TDoubleDoubleMapDecorator(tdoubledoublemap);
    }

    public static Map<Double, Float> wrap(TDoubleFloatMap tdoublefloatmap) {
        return new TDoubleFloatMapDecorator(tdoublefloatmap);
    }

    public static Map<Double, Integer> wrap(TDoubleIntMap tdoubleintmap) {
        return new TDoubleIntMapDecorator(tdoubleintmap);
    }

    public static Map<Double, Long> wrap(TDoubleLongMap tdoublelongmap) {
        return new TDoubleLongMapDecorator(tdoublelongmap);
    }

    public static Map<Double, Byte> wrap(TDoubleByteMap tdoublebytemap) {
        return new TDoubleByteMapDecorator(tdoublebytemap);
    }

    public static Map<Double, Short> wrap(TDoubleShortMap tdoubleshortmap) {
        return new TDoubleShortMapDecorator(tdoubleshortmap);
    }

    public static Map<Double, Character> wrap(TDoubleCharMap tdoublecharmap) {
        return new TDoubleCharMapDecorator(tdoublecharmap);
    }

    public static Map<Float, Double> wrap(TFloatDoubleMap tfloatdoublemap) {
        return new TFloatDoubleMapDecorator(tfloatdoublemap);
    }

    public static Map<Float, Float> wrap(TFloatFloatMap tfloatfloatmap) {
        return new TFloatFloatMapDecorator(tfloatfloatmap);
    }

    public static Map<Float, Integer> wrap(TFloatIntMap tfloatintmap) {
        return new TFloatIntMapDecorator(tfloatintmap);
    }

    public static Map<Float, Long> wrap(TFloatLongMap tfloatlongmap) {
        return new TFloatLongMapDecorator(tfloatlongmap);
    }

    public static Map<Float, Byte> wrap(TFloatByteMap tfloatbytemap) {
        return new TFloatByteMapDecorator(tfloatbytemap);
    }

    public static Map<Float, Short> wrap(TFloatShortMap tfloatshortmap) {
        return new TFloatShortMapDecorator(tfloatshortmap);
    }

    public static Map<Float, Character> wrap(TFloatCharMap tfloatcharmap) {
        return new TFloatCharMapDecorator(tfloatcharmap);
    }

    public static Map<Integer, Double> wrap(TIntDoubleMap tintdoublemap) {
        return new TIntDoubleMapDecorator(tintdoublemap);
    }

    public static Map<Integer, Float> wrap(TIntFloatMap tintfloatmap) {
        return new TIntFloatMapDecorator(tintfloatmap);
    }

    public static Map<Integer, Integer> wrap(TIntIntMap tintintmap) {
        return new TIntIntMapDecorator(tintintmap);
    }

    public static Map<Integer, Long> wrap(TIntLongMap tintlongmap) {
        return new TIntLongMapDecorator(tintlongmap);
    }

    public static Map<Integer, Byte> wrap(TIntByteMap tintbytemap) {
        return new TIntByteMapDecorator(tintbytemap);
    }

    public static Map<Integer, Short> wrap(TIntShortMap tintshortmap) {
        return new TIntShortMapDecorator(tintshortmap);
    }

    public static Map<Integer, Character> wrap(TIntCharMap tintcharmap) {
        return new TIntCharMapDecorator(tintcharmap);
    }

    public static Map<Long, Double> wrap(TLongDoubleMap tlongdoublemap) {
        return new TLongDoubleMapDecorator(tlongdoublemap);
    }

    public static Map<Long, Float> wrap(TLongFloatMap tlongfloatmap) {
        return new TLongFloatMapDecorator(tlongfloatmap);
    }

    public static Map<Long, Integer> wrap(TLongIntMap tlongintmap) {
        return new TLongIntMapDecorator(tlongintmap);
    }

    public static Map<Long, Long> wrap(TLongLongMap tlonglongmap) {
        return new TLongLongMapDecorator(tlonglongmap);
    }

    public static Map<Long, Byte> wrap(TLongByteMap tlongbytemap) {
        return new TLongByteMapDecorator(tlongbytemap);
    }

    public static Map<Long, Short> wrap(TLongShortMap tlongshortmap) {
        return new TLongShortMapDecorator(tlongshortmap);
    }

    public static Map<Long, Character> wrap(TLongCharMap tlongcharmap) {
        return new TLongCharMapDecorator(tlongcharmap);
    }

    public static Map<Byte, Double> wrap(TByteDoubleMap tbytedoublemap) {
        return new TByteDoubleMapDecorator(tbytedoublemap);
    }

    public static Map<Byte, Float> wrap(TByteFloatMap tbytefloatmap) {
        return new TByteFloatMapDecorator(tbytefloatmap);
    }

    public static Map<Byte, Integer> wrap(TByteIntMap tbyteintmap) {
        return new TByteIntMapDecorator(tbyteintmap);
    }

    public static Map<Byte, Long> wrap(TByteLongMap tbytelongmap) {
        return new TByteLongMapDecorator(tbytelongmap);
    }

    public static Map<Byte, Byte> wrap(TByteByteMap tbytebytemap) {
        return new TByteByteMapDecorator(tbytebytemap);
    }

    public static Map<Byte, Short> wrap(TByteShortMap tbyteshortmap) {
        return new TByteShortMapDecorator(tbyteshortmap);
    }

    public static Map<Byte, Character> wrap(TByteCharMap tbytecharmap) {
        return new TByteCharMapDecorator(tbytecharmap);
    }

    public static Map<Short, Double> wrap(TShortDoubleMap tshortdoublemap) {
        return new TShortDoubleMapDecorator(tshortdoublemap);
    }

    public static Map<Short, Float> wrap(TShortFloatMap tshortfloatmap) {
        return new TShortFloatMapDecorator(tshortfloatmap);
    }

    public static Map<Short, Integer> wrap(TShortIntMap tshortintmap) {
        return new TShortIntMapDecorator(tshortintmap);
    }

    public static Map<Short, Long> wrap(TShortLongMap tshortlongmap) {
        return new TShortLongMapDecorator(tshortlongmap);
    }

    public static Map<Short, Byte> wrap(TShortByteMap tshortbytemap) {
        return new TShortByteMapDecorator(tshortbytemap);
    }

    public static Map<Short, Short> wrap(TShortShortMap tshortshortmap) {
        return new TShortShortMapDecorator(tshortshortmap);
    }

    public static Map<Short, Character> wrap(TShortCharMap tshortcharmap) {
        return new TShortCharMapDecorator(tshortcharmap);
    }

    public static Map<Character, Double> wrap(TCharDoubleMap tchardoublemap) {
        return new TCharDoubleMapDecorator(tchardoublemap);
    }

    public static Map<Character, Float> wrap(TCharFloatMap tcharfloatmap) {
        return new TCharFloatMapDecorator(tcharfloatmap);
    }

    public static Map<Character, Integer> wrap(TCharIntMap tcharintmap) {
        return new TCharIntMapDecorator(tcharintmap);
    }

    public static Map<Character, Long> wrap(TCharLongMap tcharlongmap) {
        return new TCharLongMapDecorator(tcharlongmap);
    }

    public static Map<Character, Byte> wrap(TCharByteMap tcharbytemap) {
        return new TCharByteMapDecorator(tcharbytemap);
    }

    public static Map<Character, Short> wrap(TCharShortMap tcharshortmap) {
        return new TCharShortMapDecorator(tcharshortmap);
    }

    public static Map<Character, Character> wrap(TCharCharMap tcharcharmap) {
        return new TCharCharMapDecorator(tcharcharmap);
    }

    public static <T> Map<T, Double> wrap(TObjectDoubleMap<T> tobjectdoublemap) {
        return new TObjectDoubleMapDecorator<>(tobjectdoublemap);
    }

    public static <T> Map<T, Float> wrap(TObjectFloatMap<T> tobjectfloatmap) {
        return new TObjectFloatMapDecorator<>(tobjectfloatmap);
    }

    public static <T> Map<T, Integer> wrap(TObjectIntMap<T> tobjectintmap) {
        return new TObjectIntMapDecorator<>(tobjectintmap);
    }

    public static <T> Map<T, Long> wrap(TObjectLongMap<T> tobjectlongmap) {
        return new TObjectLongMapDecorator<>(tobjectlongmap);
    }

    public static <T> Map<T, Byte> wrap(TObjectByteMap<T> tobjectbytemap) {
        return new TObjectByteMapDecorator<>(tobjectbytemap);
    }

    public static <T> Map<T, Short> wrap(TObjectShortMap<T> tobjectshortmap) {
        return new TObjectShortMapDecorator<>(tobjectshortmap);
    }

    public static <T> Map<T, Character> wrap(TObjectCharMap<T> tobjectcharmap) {
        return new TObjectCharMapDecorator<>(tobjectcharmap);
    }

    public static <T> Map<Double, T> wrap(TDoubleObjectMap<T> tdoubleobjectmap) {
        return new TDoubleObjectMapDecorator<>(tdoubleobjectmap);
    }

    public static <T> Map<Float, T> wrap(TFloatObjectMap<T> tfloatobjectmap) {
        return new TFloatObjectMapDecorator<>(tfloatobjectmap);
    }

    public static <T> Map<Integer, T> wrap(TIntObjectMap<T> tintobjectmap) {
        return new TIntObjectMapDecorator<>(tintobjectmap);
    }

    public static <T> Map<Long, T> wrap(TLongObjectMap<T> tlongobjectmap) {
        return new TLongObjectMapDecorator<>(tlongobjectmap);
    }

    public static <T> Map<Byte, T> wrap(TByteObjectMap<T> tbyteobjectmap) {
        return new TByteObjectMapDecorator<>(tbyteobjectmap);
    }

    public static <T> Map<Short, T> wrap(TShortObjectMap<T> tshortobjectmap) {
        return new TShortObjectMapDecorator<>(tshortobjectmap);
    }

    public static <T> Map<Character, T> wrap(TCharObjectMap<T> tcharobjectmap) {
        return new TCharObjectMapDecorator<>(tcharobjectmap);
    }

    public static Set<Double> wrap(TDoubleSet tdoubleset) {
        return new TDoubleSetDecorator(tdoubleset);
    }

    public static Set<Float> wrap(TFloatSet tfloatset) {
        return new TFloatSetDecorator(tfloatset);
    }

    public static Set<Integer> wrap(TIntSet tintset) {
        return new TIntSetDecorator(tintset);
    }

    public static Set<Long> wrap(TLongSet tlongset) {
        return new TLongSetDecorator(tlongset);
    }

    public static Set<Byte> wrap(TByteSet tbyteset) {
        return new TByteSetDecorator(tbyteset);
    }

    public static Set<Short> wrap(TShortSet tshortset) {
        return new TShortSetDecorator(tshortset);
    }

    public static Set<Character> wrap(TCharSet tcharset) {
        return new TCharSetDecorator(tcharset);
    }

    public static List<Double> wrap(TDoubleList tdoublelist) {
        return new TDoubleListDecorator(tdoublelist);
    }

    public static List<Float> wrap(TFloatList tfloatlist) {
        return new TFloatListDecorator(tfloatlist);
    }

    public static List<Integer> wrap(TIntList tintlist) {
        return new TIntListDecorator(tintlist);
    }

    public static List<Long> wrap(TLongList tlonglist) {
        return new TLongListDecorator(tlonglist);
    }

    public static List<Byte> wrap(TByteList tbytelist) {
        return new TByteListDecorator(tbytelist);
    }

    public static List<Short> wrap(TShortList tshortlist) {
        return new TShortListDecorator(tshortlist);
    }

    public static List<Character> wrap(TCharList tcharlist) {
        return new TCharListDecorator(tcharlist);
    }
}

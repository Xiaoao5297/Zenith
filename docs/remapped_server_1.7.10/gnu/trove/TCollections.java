package gnu.trove;

import gnu.trove.impl.sync.TSynchronizedByteByteMap;
import gnu.trove.impl.sync.TSynchronizedByteCharMap;
import gnu.trove.impl.sync.TSynchronizedByteCollection;
import gnu.trove.impl.sync.TSynchronizedByteDoubleMap;
import gnu.trove.impl.sync.TSynchronizedByteFloatMap;
import gnu.trove.impl.sync.TSynchronizedByteIntMap;
import gnu.trove.impl.sync.TSynchronizedByteList;
import gnu.trove.impl.sync.TSynchronizedByteLongMap;
import gnu.trove.impl.sync.TSynchronizedByteObjectMap;
import gnu.trove.impl.sync.TSynchronizedByteSet;
import gnu.trove.impl.sync.TSynchronizedByteShortMap;
import gnu.trove.impl.sync.TSynchronizedCharByteMap;
import gnu.trove.impl.sync.TSynchronizedCharCharMap;
import gnu.trove.impl.sync.TSynchronizedCharCollection;
import gnu.trove.impl.sync.TSynchronizedCharDoubleMap;
import gnu.trove.impl.sync.TSynchronizedCharFloatMap;
import gnu.trove.impl.sync.TSynchronizedCharIntMap;
import gnu.trove.impl.sync.TSynchronizedCharList;
import gnu.trove.impl.sync.TSynchronizedCharLongMap;
import gnu.trove.impl.sync.TSynchronizedCharObjectMap;
import gnu.trove.impl.sync.TSynchronizedCharSet;
import gnu.trove.impl.sync.TSynchronizedCharShortMap;
import gnu.trove.impl.sync.TSynchronizedDoubleByteMap;
import gnu.trove.impl.sync.TSynchronizedDoubleCharMap;
import gnu.trove.impl.sync.TSynchronizedDoubleCollection;
import gnu.trove.impl.sync.TSynchronizedDoubleDoubleMap;
import gnu.trove.impl.sync.TSynchronizedDoubleFloatMap;
import gnu.trove.impl.sync.TSynchronizedDoubleIntMap;
import gnu.trove.impl.sync.TSynchronizedDoubleList;
import gnu.trove.impl.sync.TSynchronizedDoubleLongMap;
import gnu.trove.impl.sync.TSynchronizedDoubleObjectMap;
import gnu.trove.impl.sync.TSynchronizedDoubleSet;
import gnu.trove.impl.sync.TSynchronizedDoubleShortMap;
import gnu.trove.impl.sync.TSynchronizedFloatByteMap;
import gnu.trove.impl.sync.TSynchronizedFloatCharMap;
import gnu.trove.impl.sync.TSynchronizedFloatCollection;
import gnu.trove.impl.sync.TSynchronizedFloatDoubleMap;
import gnu.trove.impl.sync.TSynchronizedFloatFloatMap;
import gnu.trove.impl.sync.TSynchronizedFloatIntMap;
import gnu.trove.impl.sync.TSynchronizedFloatList;
import gnu.trove.impl.sync.TSynchronizedFloatLongMap;
import gnu.trove.impl.sync.TSynchronizedFloatObjectMap;
import gnu.trove.impl.sync.TSynchronizedFloatSet;
import gnu.trove.impl.sync.TSynchronizedFloatShortMap;
import gnu.trove.impl.sync.TSynchronizedIntByteMap;
import gnu.trove.impl.sync.TSynchronizedIntCharMap;
import gnu.trove.impl.sync.TSynchronizedIntCollection;
import gnu.trove.impl.sync.TSynchronizedIntDoubleMap;
import gnu.trove.impl.sync.TSynchronizedIntFloatMap;
import gnu.trove.impl.sync.TSynchronizedIntIntMap;
import gnu.trove.impl.sync.TSynchronizedIntList;
import gnu.trove.impl.sync.TSynchronizedIntLongMap;
import gnu.trove.impl.sync.TSynchronizedIntObjectMap;
import gnu.trove.impl.sync.TSynchronizedIntSet;
import gnu.trove.impl.sync.TSynchronizedIntShortMap;
import gnu.trove.impl.sync.TSynchronizedLongByteMap;
import gnu.trove.impl.sync.TSynchronizedLongCharMap;
import gnu.trove.impl.sync.TSynchronizedLongCollection;
import gnu.trove.impl.sync.TSynchronizedLongDoubleMap;
import gnu.trove.impl.sync.TSynchronizedLongFloatMap;
import gnu.trove.impl.sync.TSynchronizedLongIntMap;
import gnu.trove.impl.sync.TSynchronizedLongList;
import gnu.trove.impl.sync.TSynchronizedLongLongMap;
import gnu.trove.impl.sync.TSynchronizedLongObjectMap;
import gnu.trove.impl.sync.TSynchronizedLongSet;
import gnu.trove.impl.sync.TSynchronizedLongShortMap;
import gnu.trove.impl.sync.TSynchronizedObjectByteMap;
import gnu.trove.impl.sync.TSynchronizedObjectCharMap;
import gnu.trove.impl.sync.TSynchronizedObjectDoubleMap;
import gnu.trove.impl.sync.TSynchronizedObjectFloatMap;
import gnu.trove.impl.sync.TSynchronizedObjectIntMap;
import gnu.trove.impl.sync.TSynchronizedObjectLongMap;
import gnu.trove.impl.sync.TSynchronizedObjectShortMap;
import gnu.trove.impl.sync.TSynchronizedRandomAccessByteList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessCharList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessDoubleList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessFloatList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessIntList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessLongList;
import gnu.trove.impl.sync.TSynchronizedRandomAccessShortList;
import gnu.trove.impl.sync.TSynchronizedShortByteMap;
import gnu.trove.impl.sync.TSynchronizedShortCharMap;
import gnu.trove.impl.sync.TSynchronizedShortCollection;
import gnu.trove.impl.sync.TSynchronizedShortDoubleMap;
import gnu.trove.impl.sync.TSynchronizedShortFloatMap;
import gnu.trove.impl.sync.TSynchronizedShortIntMap;
import gnu.trove.impl.sync.TSynchronizedShortList;
import gnu.trove.impl.sync.TSynchronizedShortLongMap;
import gnu.trove.impl.sync.TSynchronizedShortObjectMap;
import gnu.trove.impl.sync.TSynchronizedShortSet;
import gnu.trove.impl.sync.TSynchronizedShortShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteList;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableByteShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharList;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableCharShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleList;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableDoubleShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatList;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableFloatShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntList;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableIntShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongList;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableLongShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableObjectShortMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessByteList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessCharList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessDoubleList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessFloatList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessIntList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessLongList;
import gnu.trove.impl.unmodifiable.TUnmodifiableRandomAccessShortList;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortByteMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortCharMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortCollection;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortDoubleMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortFloatMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortIntMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortList;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortLongMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortObjectMap;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortSet;
import gnu.trove.impl.unmodifiable.TUnmodifiableShortShortMap;
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
import java.util.RandomAccess;

public class TCollections {

    private TCollections() {}

    public static TDoubleCollection unmodifiableCollection(TDoubleCollection tdoublecollection) {
        return new TUnmodifiableDoubleCollection(tdoublecollection);
    }

    public static TFloatCollection unmodifiableCollection(TFloatCollection tfloatcollection) {
        return new TUnmodifiableFloatCollection(tfloatcollection);
    }

    public static TIntCollection unmodifiableCollection(TIntCollection tintcollection) {
        return new TUnmodifiableIntCollection(tintcollection);
    }

    public static TLongCollection unmodifiableCollection(TLongCollection tlongcollection) {
        return new TUnmodifiableLongCollection(tlongcollection);
    }

    public static TByteCollection unmodifiableCollection(TByteCollection tbytecollection) {
        return new TUnmodifiableByteCollection(tbytecollection);
    }

    public static TShortCollection unmodifiableCollection(TShortCollection tshortcollection) {
        return new TUnmodifiableShortCollection(tshortcollection);
    }

    public static TCharCollection unmodifiableCollection(TCharCollection tcharcollection) {
        return new TUnmodifiableCharCollection(tcharcollection);
    }

    public static TDoubleSet unmodifiableSet(TDoubleSet tdoubleset) {
        return new TUnmodifiableDoubleSet(tdoubleset);
    }

    public static TFloatSet unmodifiableSet(TFloatSet tfloatset) {
        return new TUnmodifiableFloatSet(tfloatset);
    }

    public static TIntSet unmodifiableSet(TIntSet tintset) {
        return new TUnmodifiableIntSet(tintset);
    }

    public static TLongSet unmodifiableSet(TLongSet tlongset) {
        return new TUnmodifiableLongSet(tlongset);
    }

    public static TByteSet unmodifiableSet(TByteSet tbyteset) {
        return new TUnmodifiableByteSet(tbyteset);
    }

    public static TShortSet unmodifiableSet(TShortSet tshortset) {
        return new TUnmodifiableShortSet(tshortset);
    }

    public static TCharSet unmodifiableSet(TCharSet tcharset) {
        return new TUnmodifiableCharSet(tcharset);
    }

    public static TDoubleList unmodifiableList(TDoubleList tdoublelist) {
        return (TDoubleList) (tdoublelist instanceof RandomAccess ? new TUnmodifiableRandomAccessDoubleList(tdoublelist) : new TUnmodifiableDoubleList(tdoublelist));
    }

    public static TFloatList unmodifiableList(TFloatList tfloatlist) {
        return (TFloatList) (tfloatlist instanceof RandomAccess ? new TUnmodifiableRandomAccessFloatList(tfloatlist) : new TUnmodifiableFloatList(tfloatlist));
    }

    public static TIntList unmodifiableList(TIntList tintlist) {
        return (TIntList) (tintlist instanceof RandomAccess ? new TUnmodifiableRandomAccessIntList(tintlist) : new TUnmodifiableIntList(tintlist));
    }

    public static TLongList unmodifiableList(TLongList tlonglist) {
        return (TLongList) (tlonglist instanceof RandomAccess ? new TUnmodifiableRandomAccessLongList(tlonglist) : new TUnmodifiableLongList(tlonglist));
    }

    public static TByteList unmodifiableList(TByteList tbytelist) {
        return (TByteList) (tbytelist instanceof RandomAccess ? new TUnmodifiableRandomAccessByteList(tbytelist) : new TUnmodifiableByteList(tbytelist));
    }

    public static TShortList unmodifiableList(TShortList tshortlist) {
        return (TShortList) (tshortlist instanceof RandomAccess ? new TUnmodifiableRandomAccessShortList(tshortlist) : new TUnmodifiableShortList(tshortlist));
    }

    public static TCharList unmodifiableList(TCharList tcharlist) {
        return (TCharList) (tcharlist instanceof RandomAccess ? new TUnmodifiableRandomAccessCharList(tcharlist) : new TUnmodifiableCharList(tcharlist));
    }

    public static TDoubleDoubleMap unmodifiableMap(TDoubleDoubleMap tdoubledoublemap) {
        return new TUnmodifiableDoubleDoubleMap(tdoubledoublemap);
    }

    public static TDoubleFloatMap unmodifiableMap(TDoubleFloatMap tdoublefloatmap) {
        return new TUnmodifiableDoubleFloatMap(tdoublefloatmap);
    }

    public static TDoubleIntMap unmodifiableMap(TDoubleIntMap tdoubleintmap) {
        return new TUnmodifiableDoubleIntMap(tdoubleintmap);
    }

    public static TDoubleLongMap unmodifiableMap(TDoubleLongMap tdoublelongmap) {
        return new TUnmodifiableDoubleLongMap(tdoublelongmap);
    }

    public static TDoubleByteMap unmodifiableMap(TDoubleByteMap tdoublebytemap) {
        return new TUnmodifiableDoubleByteMap(tdoublebytemap);
    }

    public static TDoubleShortMap unmodifiableMap(TDoubleShortMap tdoubleshortmap) {
        return new TUnmodifiableDoubleShortMap(tdoubleshortmap);
    }

    public static TDoubleCharMap unmodifiableMap(TDoubleCharMap tdoublecharmap) {
        return new TUnmodifiableDoubleCharMap(tdoublecharmap);
    }

    public static TFloatDoubleMap unmodifiableMap(TFloatDoubleMap tfloatdoublemap) {
        return new TUnmodifiableFloatDoubleMap(tfloatdoublemap);
    }

    public static TFloatFloatMap unmodifiableMap(TFloatFloatMap tfloatfloatmap) {
        return new TUnmodifiableFloatFloatMap(tfloatfloatmap);
    }

    public static TFloatIntMap unmodifiableMap(TFloatIntMap tfloatintmap) {
        return new TUnmodifiableFloatIntMap(tfloatintmap);
    }

    public static TFloatLongMap unmodifiableMap(TFloatLongMap tfloatlongmap) {
        return new TUnmodifiableFloatLongMap(tfloatlongmap);
    }

    public static TFloatByteMap unmodifiableMap(TFloatByteMap tfloatbytemap) {
        return new TUnmodifiableFloatByteMap(tfloatbytemap);
    }

    public static TFloatShortMap unmodifiableMap(TFloatShortMap tfloatshortmap) {
        return new TUnmodifiableFloatShortMap(tfloatshortmap);
    }

    public static TFloatCharMap unmodifiableMap(TFloatCharMap tfloatcharmap) {
        return new TUnmodifiableFloatCharMap(tfloatcharmap);
    }

    public static TIntDoubleMap unmodifiableMap(TIntDoubleMap tintdoublemap) {
        return new TUnmodifiableIntDoubleMap(tintdoublemap);
    }

    public static TIntFloatMap unmodifiableMap(TIntFloatMap tintfloatmap) {
        return new TUnmodifiableIntFloatMap(tintfloatmap);
    }

    public static TIntIntMap unmodifiableMap(TIntIntMap tintintmap) {
        return new TUnmodifiableIntIntMap(tintintmap);
    }

    public static TIntLongMap unmodifiableMap(TIntLongMap tintlongmap) {
        return new TUnmodifiableIntLongMap(tintlongmap);
    }

    public static TIntByteMap unmodifiableMap(TIntByteMap tintbytemap) {
        return new TUnmodifiableIntByteMap(tintbytemap);
    }

    public static TIntShortMap unmodifiableMap(TIntShortMap tintshortmap) {
        return new TUnmodifiableIntShortMap(tintshortmap);
    }

    public static TIntCharMap unmodifiableMap(TIntCharMap tintcharmap) {
        return new TUnmodifiableIntCharMap(tintcharmap);
    }

    public static TLongDoubleMap unmodifiableMap(TLongDoubleMap tlongdoublemap) {
        return new TUnmodifiableLongDoubleMap(tlongdoublemap);
    }

    public static TLongFloatMap unmodifiableMap(TLongFloatMap tlongfloatmap) {
        return new TUnmodifiableLongFloatMap(tlongfloatmap);
    }

    public static TLongIntMap unmodifiableMap(TLongIntMap tlongintmap) {
        return new TUnmodifiableLongIntMap(tlongintmap);
    }

    public static TLongLongMap unmodifiableMap(TLongLongMap tlonglongmap) {
        return new TUnmodifiableLongLongMap(tlonglongmap);
    }

    public static TLongByteMap unmodifiableMap(TLongByteMap tlongbytemap) {
        return new TUnmodifiableLongByteMap(tlongbytemap);
    }

    public static TLongShortMap unmodifiableMap(TLongShortMap tlongshortmap) {
        return new TUnmodifiableLongShortMap(tlongshortmap);
    }

    public static TLongCharMap unmodifiableMap(TLongCharMap tlongcharmap) {
        return new TUnmodifiableLongCharMap(tlongcharmap);
    }

    public static TByteDoubleMap unmodifiableMap(TByteDoubleMap tbytedoublemap) {
        return new TUnmodifiableByteDoubleMap(tbytedoublemap);
    }

    public static TByteFloatMap unmodifiableMap(TByteFloatMap tbytefloatmap) {
        return new TUnmodifiableByteFloatMap(tbytefloatmap);
    }

    public static TByteIntMap unmodifiableMap(TByteIntMap tbyteintmap) {
        return new TUnmodifiableByteIntMap(tbyteintmap);
    }

    public static TByteLongMap unmodifiableMap(TByteLongMap tbytelongmap) {
        return new TUnmodifiableByteLongMap(tbytelongmap);
    }

    public static TByteByteMap unmodifiableMap(TByteByteMap tbytebytemap) {
        return new TUnmodifiableByteByteMap(tbytebytemap);
    }

    public static TByteShortMap unmodifiableMap(TByteShortMap tbyteshortmap) {
        return new TUnmodifiableByteShortMap(tbyteshortmap);
    }

    public static TByteCharMap unmodifiableMap(TByteCharMap tbytecharmap) {
        return new TUnmodifiableByteCharMap(tbytecharmap);
    }

    public static TShortDoubleMap unmodifiableMap(TShortDoubleMap tshortdoublemap) {
        return new TUnmodifiableShortDoubleMap(tshortdoublemap);
    }

    public static TShortFloatMap unmodifiableMap(TShortFloatMap tshortfloatmap) {
        return new TUnmodifiableShortFloatMap(tshortfloatmap);
    }

    public static TShortIntMap unmodifiableMap(TShortIntMap tshortintmap) {
        return new TUnmodifiableShortIntMap(tshortintmap);
    }

    public static TShortLongMap unmodifiableMap(TShortLongMap tshortlongmap) {
        return new TUnmodifiableShortLongMap(tshortlongmap);
    }

    public static TShortByteMap unmodifiableMap(TShortByteMap tshortbytemap) {
        return new TUnmodifiableShortByteMap(tshortbytemap);
    }

    public static TShortShortMap unmodifiableMap(TShortShortMap tshortshortmap) {
        return new TUnmodifiableShortShortMap(tshortshortmap);
    }

    public static TShortCharMap unmodifiableMap(TShortCharMap tshortcharmap) {
        return new TUnmodifiableShortCharMap(tshortcharmap);
    }

    public static TCharDoubleMap unmodifiableMap(TCharDoubleMap tchardoublemap) {
        return new TUnmodifiableCharDoubleMap(tchardoublemap);
    }

    public static TCharFloatMap unmodifiableMap(TCharFloatMap tcharfloatmap) {
        return new TUnmodifiableCharFloatMap(tcharfloatmap);
    }

    public static TCharIntMap unmodifiableMap(TCharIntMap tcharintmap) {
        return new TUnmodifiableCharIntMap(tcharintmap);
    }

    public static TCharLongMap unmodifiableMap(TCharLongMap tcharlongmap) {
        return new TUnmodifiableCharLongMap(tcharlongmap);
    }

    public static TCharByteMap unmodifiableMap(TCharByteMap tcharbytemap) {
        return new TUnmodifiableCharByteMap(tcharbytemap);
    }

    public static TCharShortMap unmodifiableMap(TCharShortMap tcharshortmap) {
        return new TUnmodifiableCharShortMap(tcharshortmap);
    }

    public static TCharCharMap unmodifiableMap(TCharCharMap tcharcharmap) {
        return new TUnmodifiableCharCharMap(tcharcharmap);
    }

    public static <V> TDoubleObjectMap<V> unmodifiableMap(TDoubleObjectMap<V> tdoubleobjectmap) {
        return new TUnmodifiableDoubleObjectMap<>(tdoubleobjectmap);
    }

    public static <V> TFloatObjectMap<V> unmodifiableMap(TFloatObjectMap<V> tfloatobjectmap) {
        return new TUnmodifiableFloatObjectMap<>(tfloatobjectmap);
    }

    public static <V> TIntObjectMap<V> unmodifiableMap(TIntObjectMap<V> tintobjectmap) {
        return new TUnmodifiableIntObjectMap<>(tintobjectmap);
    }

    public static <V> TLongObjectMap<V> unmodifiableMap(TLongObjectMap<V> tlongobjectmap) {
        return new TUnmodifiableLongObjectMap<>(tlongobjectmap);
    }

    public static <V> TByteObjectMap<V> unmodifiableMap(TByteObjectMap<V> tbyteobjectmap) {
        return new TUnmodifiableByteObjectMap<>(tbyteobjectmap);
    }

    public static <V> TShortObjectMap<V> unmodifiableMap(TShortObjectMap<V> tshortobjectmap) {
        return new TUnmodifiableShortObjectMap<>(tshortobjectmap);
    }

    public static <V> TCharObjectMap<V> unmodifiableMap(TCharObjectMap<V> tcharobjectmap) {
        return new TUnmodifiableCharObjectMap<>(tcharobjectmap);
    }

    public static <K> TObjectDoubleMap<K> unmodifiableMap(TObjectDoubleMap<K> tobjectdoublemap) {
        return new TUnmodifiableObjectDoubleMap<>(tobjectdoublemap);
    }

    public static <K> TObjectFloatMap<K> unmodifiableMap(TObjectFloatMap<K> tobjectfloatmap) {
        return new TUnmodifiableObjectFloatMap<>(tobjectfloatmap);
    }

    public static <K> TObjectIntMap<K> unmodifiableMap(TObjectIntMap<K> tobjectintmap) {
        return new TUnmodifiableObjectIntMap<>(tobjectintmap);
    }

    public static <K> TObjectLongMap<K> unmodifiableMap(TObjectLongMap<K> tobjectlongmap) {
        return new TUnmodifiableObjectLongMap<>(tobjectlongmap);
    }

    public static <K> TObjectByteMap<K> unmodifiableMap(TObjectByteMap<K> tobjectbytemap) {
        return new TUnmodifiableObjectByteMap<>(tobjectbytemap);
    }

    public static <K> TObjectShortMap<K> unmodifiableMap(TObjectShortMap<K> tobjectshortmap) {
        return new TUnmodifiableObjectShortMap<>(tobjectshortmap);
    }

    public static <K> TObjectCharMap<K> unmodifiableMap(TObjectCharMap<K> tobjectcharmap) {
        return new TUnmodifiableObjectCharMap<>(tobjectcharmap);
    }

    public static TDoubleCollection synchronizedCollection(TDoubleCollection tdoublecollection) {
        return new TSynchronizedDoubleCollection(tdoublecollection);
    }

    static TDoubleCollection synchronizedCollection(TDoubleCollection tdoublecollection, Object object) {
        return new TSynchronizedDoubleCollection(tdoublecollection, object);
    }

    public static TFloatCollection synchronizedCollection(TFloatCollection tfloatcollection) {
        return new TSynchronizedFloatCollection(tfloatcollection);
    }

    static TFloatCollection synchronizedCollection(TFloatCollection tfloatcollection, Object object) {
        return new TSynchronizedFloatCollection(tfloatcollection, object);
    }

    public static TIntCollection synchronizedCollection(TIntCollection tintcollection) {
        return new TSynchronizedIntCollection(tintcollection);
    }

    static TIntCollection synchronizedCollection(TIntCollection tintcollection, Object object) {
        return new TSynchronizedIntCollection(tintcollection, object);
    }

    public static TLongCollection synchronizedCollection(TLongCollection tlongcollection) {
        return new TSynchronizedLongCollection(tlongcollection);
    }

    static TLongCollection synchronizedCollection(TLongCollection tlongcollection, Object object) {
        return new TSynchronizedLongCollection(tlongcollection, object);
    }

    public static TByteCollection synchronizedCollection(TByteCollection tbytecollection) {
        return new TSynchronizedByteCollection(tbytecollection);
    }

    static TByteCollection synchronizedCollection(TByteCollection tbytecollection, Object object) {
        return new TSynchronizedByteCollection(tbytecollection, object);
    }

    public static TShortCollection synchronizedCollection(TShortCollection tshortcollection) {
        return new TSynchronizedShortCollection(tshortcollection);
    }

    static TShortCollection synchronizedCollection(TShortCollection tshortcollection, Object object) {
        return new TSynchronizedShortCollection(tshortcollection, object);
    }

    public static TCharCollection synchronizedCollection(TCharCollection tcharcollection) {
        return new TSynchronizedCharCollection(tcharcollection);
    }

    static TCharCollection synchronizedCollection(TCharCollection tcharcollection, Object object) {
        return new TSynchronizedCharCollection(tcharcollection, object);
    }

    public static TDoubleSet synchronizedSet(TDoubleSet tdoubleset) {
        return new TSynchronizedDoubleSet(tdoubleset);
    }

    static TDoubleSet synchronizedSet(TDoubleSet tdoubleset, Object object) {
        return new TSynchronizedDoubleSet(tdoubleset, object);
    }

    public static TFloatSet synchronizedSet(TFloatSet tfloatset) {
        return new TSynchronizedFloatSet(tfloatset);
    }

    static TFloatSet synchronizedSet(TFloatSet tfloatset, Object object) {
        return new TSynchronizedFloatSet(tfloatset, object);
    }

    public static TIntSet synchronizedSet(TIntSet tintset) {
        return new TSynchronizedIntSet(tintset);
    }

    static TIntSet synchronizedSet(TIntSet tintset, Object object) {
        return new TSynchronizedIntSet(tintset, object);
    }

    public static TLongSet synchronizedSet(TLongSet tlongset) {
        return new TSynchronizedLongSet(tlongset);
    }

    static TLongSet synchronizedSet(TLongSet tlongset, Object object) {
        return new TSynchronizedLongSet(tlongset, object);
    }

    public static TByteSet synchronizedSet(TByteSet tbyteset) {
        return new TSynchronizedByteSet(tbyteset);
    }

    static TByteSet synchronizedSet(TByteSet tbyteset, Object object) {
        return new TSynchronizedByteSet(tbyteset, object);
    }

    public static TShortSet synchronizedSet(TShortSet tshortset) {
        return new TSynchronizedShortSet(tshortset);
    }

    static TShortSet synchronizedSet(TShortSet tshortset, Object object) {
        return new TSynchronizedShortSet(tshortset, object);
    }

    public static TCharSet synchronizedSet(TCharSet tcharset) {
        return new TSynchronizedCharSet(tcharset);
    }

    static TCharSet synchronizedSet(TCharSet tcharset, Object object) {
        return new TSynchronizedCharSet(tcharset, object);
    }

    public static TDoubleList synchronizedList(TDoubleList tdoublelist) {
        return (TDoubleList) (tdoublelist instanceof RandomAccess ? new TSynchronizedRandomAccessDoubleList(tdoublelist) : new TSynchronizedDoubleList(tdoublelist));
    }

    static TDoubleList synchronizedList(TDoubleList tdoublelist, Object object) {
        return (TDoubleList) (tdoublelist instanceof RandomAccess ? new TSynchronizedRandomAccessDoubleList(tdoublelist, object) : new TSynchronizedDoubleList(tdoublelist, object));
    }

    public static TFloatList synchronizedList(TFloatList tfloatlist) {
        return (TFloatList) (tfloatlist instanceof RandomAccess ? new TSynchronizedRandomAccessFloatList(tfloatlist) : new TSynchronizedFloatList(tfloatlist));
    }

    static TFloatList synchronizedList(TFloatList tfloatlist, Object object) {
        return (TFloatList) (tfloatlist instanceof RandomAccess ? new TSynchronizedRandomAccessFloatList(tfloatlist, object) : new TSynchronizedFloatList(tfloatlist, object));
    }

    public static TIntList synchronizedList(TIntList tintlist) {
        return (TIntList) (tintlist instanceof RandomAccess ? new TSynchronizedRandomAccessIntList(tintlist) : new TSynchronizedIntList(tintlist));
    }

    static TIntList synchronizedList(TIntList tintlist, Object object) {
        return (TIntList) (tintlist instanceof RandomAccess ? new TSynchronizedRandomAccessIntList(tintlist, object) : new TSynchronizedIntList(tintlist, object));
    }

    public static TLongList synchronizedList(TLongList tlonglist) {
        return (TLongList) (tlonglist instanceof RandomAccess ? new TSynchronizedRandomAccessLongList(tlonglist) : new TSynchronizedLongList(tlonglist));
    }

    static TLongList synchronizedList(TLongList tlonglist, Object object) {
        return (TLongList) (tlonglist instanceof RandomAccess ? new TSynchronizedRandomAccessLongList(tlonglist, object) : new TSynchronizedLongList(tlonglist, object));
    }

    public static TByteList synchronizedList(TByteList tbytelist) {
        return (TByteList) (tbytelist instanceof RandomAccess ? new TSynchronizedRandomAccessByteList(tbytelist) : new TSynchronizedByteList(tbytelist));
    }

    static TByteList synchronizedList(TByteList tbytelist, Object object) {
        return (TByteList) (tbytelist instanceof RandomAccess ? new TSynchronizedRandomAccessByteList(tbytelist, object) : new TSynchronizedByteList(tbytelist, object));
    }

    public static TShortList synchronizedList(TShortList tshortlist) {
        return (TShortList) (tshortlist instanceof RandomAccess ? new TSynchronizedRandomAccessShortList(tshortlist) : new TSynchronizedShortList(tshortlist));
    }

    static TShortList synchronizedList(TShortList tshortlist, Object object) {
        return (TShortList) (tshortlist instanceof RandomAccess ? new TSynchronizedRandomAccessShortList(tshortlist, object) : new TSynchronizedShortList(tshortlist, object));
    }

    public static TCharList synchronizedList(TCharList tcharlist) {
        return (TCharList) (tcharlist instanceof RandomAccess ? new TSynchronizedRandomAccessCharList(tcharlist) : new TSynchronizedCharList(tcharlist));
    }

    static TCharList synchronizedList(TCharList tcharlist, Object object) {
        return (TCharList) (tcharlist instanceof RandomAccess ? new TSynchronizedRandomAccessCharList(tcharlist, object) : new TSynchronizedCharList(tcharlist, object));
    }

    public static TDoubleDoubleMap synchronizedMap(TDoubleDoubleMap tdoubledoublemap) {
        return new TSynchronizedDoubleDoubleMap(tdoubledoublemap);
    }

    public static TDoubleFloatMap synchronizedMap(TDoubleFloatMap tdoublefloatmap) {
        return new TSynchronizedDoubleFloatMap(tdoublefloatmap);
    }

    public static TDoubleIntMap synchronizedMap(TDoubleIntMap tdoubleintmap) {
        return new TSynchronizedDoubleIntMap(tdoubleintmap);
    }

    public static TDoubleLongMap synchronizedMap(TDoubleLongMap tdoublelongmap) {
        return new TSynchronizedDoubleLongMap(tdoublelongmap);
    }

    public static TDoubleByteMap synchronizedMap(TDoubleByteMap tdoublebytemap) {
        return new TSynchronizedDoubleByteMap(tdoublebytemap);
    }

    public static TDoubleShortMap synchronizedMap(TDoubleShortMap tdoubleshortmap) {
        return new TSynchronizedDoubleShortMap(tdoubleshortmap);
    }

    public static TDoubleCharMap synchronizedMap(TDoubleCharMap tdoublecharmap) {
        return new TSynchronizedDoubleCharMap(tdoublecharmap);
    }

    public static TFloatDoubleMap synchronizedMap(TFloatDoubleMap tfloatdoublemap) {
        return new TSynchronizedFloatDoubleMap(tfloatdoublemap);
    }

    public static TFloatFloatMap synchronizedMap(TFloatFloatMap tfloatfloatmap) {
        return new TSynchronizedFloatFloatMap(tfloatfloatmap);
    }

    public static TFloatIntMap synchronizedMap(TFloatIntMap tfloatintmap) {
        return new TSynchronizedFloatIntMap(tfloatintmap);
    }

    public static TFloatLongMap synchronizedMap(TFloatLongMap tfloatlongmap) {
        return new TSynchronizedFloatLongMap(tfloatlongmap);
    }

    public static TFloatByteMap synchronizedMap(TFloatByteMap tfloatbytemap) {
        return new TSynchronizedFloatByteMap(tfloatbytemap);
    }

    public static TFloatShortMap synchronizedMap(TFloatShortMap tfloatshortmap) {
        return new TSynchronizedFloatShortMap(tfloatshortmap);
    }

    public static TFloatCharMap synchronizedMap(TFloatCharMap tfloatcharmap) {
        return new TSynchronizedFloatCharMap(tfloatcharmap);
    }

    public static TIntDoubleMap synchronizedMap(TIntDoubleMap tintdoublemap) {
        return new TSynchronizedIntDoubleMap(tintdoublemap);
    }

    public static TIntFloatMap synchronizedMap(TIntFloatMap tintfloatmap) {
        return new TSynchronizedIntFloatMap(tintfloatmap);
    }

    public static TIntIntMap synchronizedMap(TIntIntMap tintintmap) {
        return new TSynchronizedIntIntMap(tintintmap);
    }

    public static TIntLongMap synchronizedMap(TIntLongMap tintlongmap) {
        return new TSynchronizedIntLongMap(tintlongmap);
    }

    public static TIntByteMap synchronizedMap(TIntByteMap tintbytemap) {
        return new TSynchronizedIntByteMap(tintbytemap);
    }

    public static TIntShortMap synchronizedMap(TIntShortMap tintshortmap) {
        return new TSynchronizedIntShortMap(tintshortmap);
    }

    public static TIntCharMap synchronizedMap(TIntCharMap tintcharmap) {
        return new TSynchronizedIntCharMap(tintcharmap);
    }

    public static TLongDoubleMap synchronizedMap(TLongDoubleMap tlongdoublemap) {
        return new TSynchronizedLongDoubleMap(tlongdoublemap);
    }

    public static TLongFloatMap synchronizedMap(TLongFloatMap tlongfloatmap) {
        return new TSynchronizedLongFloatMap(tlongfloatmap);
    }

    public static TLongIntMap synchronizedMap(TLongIntMap tlongintmap) {
        return new TSynchronizedLongIntMap(tlongintmap);
    }

    public static TLongLongMap synchronizedMap(TLongLongMap tlonglongmap) {
        return new TSynchronizedLongLongMap(tlonglongmap);
    }

    public static TLongByteMap synchronizedMap(TLongByteMap tlongbytemap) {
        return new TSynchronizedLongByteMap(tlongbytemap);
    }

    public static TLongShortMap synchronizedMap(TLongShortMap tlongshortmap) {
        return new TSynchronizedLongShortMap(tlongshortmap);
    }

    public static TLongCharMap synchronizedMap(TLongCharMap tlongcharmap) {
        return new TSynchronizedLongCharMap(tlongcharmap);
    }

    public static TByteDoubleMap synchronizedMap(TByteDoubleMap tbytedoublemap) {
        return new TSynchronizedByteDoubleMap(tbytedoublemap);
    }

    public static TByteFloatMap synchronizedMap(TByteFloatMap tbytefloatmap) {
        return new TSynchronizedByteFloatMap(tbytefloatmap);
    }

    public static TByteIntMap synchronizedMap(TByteIntMap tbyteintmap) {
        return new TSynchronizedByteIntMap(tbyteintmap);
    }

    public static TByteLongMap synchronizedMap(TByteLongMap tbytelongmap) {
        return new TSynchronizedByteLongMap(tbytelongmap);
    }

    public static TByteByteMap synchronizedMap(TByteByteMap tbytebytemap) {
        return new TSynchronizedByteByteMap(tbytebytemap);
    }

    public static TByteShortMap synchronizedMap(TByteShortMap tbyteshortmap) {
        return new TSynchronizedByteShortMap(tbyteshortmap);
    }

    public static TByteCharMap synchronizedMap(TByteCharMap tbytecharmap) {
        return new TSynchronizedByteCharMap(tbytecharmap);
    }

    public static TShortDoubleMap synchronizedMap(TShortDoubleMap tshortdoublemap) {
        return new TSynchronizedShortDoubleMap(tshortdoublemap);
    }

    public static TShortFloatMap synchronizedMap(TShortFloatMap tshortfloatmap) {
        return new TSynchronizedShortFloatMap(tshortfloatmap);
    }

    public static TShortIntMap synchronizedMap(TShortIntMap tshortintmap) {
        return new TSynchronizedShortIntMap(tshortintmap);
    }

    public static TShortLongMap synchronizedMap(TShortLongMap tshortlongmap) {
        return new TSynchronizedShortLongMap(tshortlongmap);
    }

    public static TShortByteMap synchronizedMap(TShortByteMap tshortbytemap) {
        return new TSynchronizedShortByteMap(tshortbytemap);
    }

    public static TShortShortMap synchronizedMap(TShortShortMap tshortshortmap) {
        return new TSynchronizedShortShortMap(tshortshortmap);
    }

    public static TShortCharMap synchronizedMap(TShortCharMap tshortcharmap) {
        return new TSynchronizedShortCharMap(tshortcharmap);
    }

    public static TCharDoubleMap synchronizedMap(TCharDoubleMap tchardoublemap) {
        return new TSynchronizedCharDoubleMap(tchardoublemap);
    }

    public static TCharFloatMap synchronizedMap(TCharFloatMap tcharfloatmap) {
        return new TSynchronizedCharFloatMap(tcharfloatmap);
    }

    public static TCharIntMap synchronizedMap(TCharIntMap tcharintmap) {
        return new TSynchronizedCharIntMap(tcharintmap);
    }

    public static TCharLongMap synchronizedMap(TCharLongMap tcharlongmap) {
        return new TSynchronizedCharLongMap(tcharlongmap);
    }

    public static TCharByteMap synchronizedMap(TCharByteMap tcharbytemap) {
        return new TSynchronizedCharByteMap(tcharbytemap);
    }

    public static TCharShortMap synchronizedMap(TCharShortMap tcharshortmap) {
        return new TSynchronizedCharShortMap(tcharshortmap);
    }

    public static TCharCharMap synchronizedMap(TCharCharMap tcharcharmap) {
        return new TSynchronizedCharCharMap(tcharcharmap);
    }

    public static <V> TDoubleObjectMap<V> synchronizedMap(TDoubleObjectMap<V> tdoubleobjectmap) {
        return new TSynchronizedDoubleObjectMap<>(tdoubleobjectmap);
    }

    public static <V> TFloatObjectMap<V> synchronizedMap(TFloatObjectMap<V> tfloatobjectmap) {
        return new TSynchronizedFloatObjectMap<>(tfloatobjectmap);
    }

    public static <V> TIntObjectMap<V> synchronizedMap(TIntObjectMap<V> tintobjectmap) {
        return new TSynchronizedIntObjectMap<>(tintobjectmap);
    }

    public static <V> TLongObjectMap<V> synchronizedMap(TLongObjectMap<V> tlongobjectmap) {
        return new TSynchronizedLongObjectMap<>(tlongobjectmap);
    }

    public static <V> TByteObjectMap<V> synchronizedMap(TByteObjectMap<V> tbyteobjectmap) {
        return new TSynchronizedByteObjectMap<>(tbyteobjectmap);
    }

    public static <V> TShortObjectMap<V> synchronizedMap(TShortObjectMap<V> tshortobjectmap) {
        return new TSynchronizedShortObjectMap<>(tshortobjectmap);
    }

    public static <V> TCharObjectMap<V> synchronizedMap(TCharObjectMap<V> tcharobjectmap) {
        return new TSynchronizedCharObjectMap<>(tcharobjectmap);
    }

    public static <K> TObjectDoubleMap<K> synchronizedMap(TObjectDoubleMap<K> tobjectdoublemap) {
        return new TSynchronizedObjectDoubleMap<>(tobjectdoublemap);
    }

    public static <K> TObjectFloatMap<K> synchronizedMap(TObjectFloatMap<K> tobjectfloatmap) {
        return new TSynchronizedObjectFloatMap<>(tobjectfloatmap);
    }

    public static <K> TObjectIntMap<K> synchronizedMap(TObjectIntMap<K> tobjectintmap) {
        return new TSynchronizedObjectIntMap<>(tobjectintmap);
    }

    public static <K> TObjectLongMap<K> synchronizedMap(TObjectLongMap<K> tobjectlongmap) {
        return new TSynchronizedObjectLongMap<>(tobjectlongmap);
    }

    public static <K> TObjectByteMap<K> synchronizedMap(TObjectByteMap<K> tobjectbytemap) {
        return new TSynchronizedObjectByteMap<>(tobjectbytemap);
    }

    public static <K> TObjectShortMap<K> synchronizedMap(TObjectShortMap<K> tobjectshortmap) {
        return new TSynchronizedObjectShortMap<>(tobjectshortmap);
    }

    public static <K> TObjectCharMap<K> synchronizedMap(TObjectCharMap<K> tobjectcharmap) {
        return new TSynchronizedObjectCharMap<>(tobjectcharmap);
    }
}

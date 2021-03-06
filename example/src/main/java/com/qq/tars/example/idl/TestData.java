// **********************************************************************
// This file was generated by a TARS parser!
// TARS version 2.4.14.
// **********************************************************************

package com.qq.tars.example.idl;

public final class TestData extends com.qq.tars.codec.TarsStruct implements Cloneable
{
    public String className()
    {
        return "micang.TestData";
    }

    public String fullClassName()
    {
        return "TestData";
    }

    public int id = 0;

    public String code = "";

    public String[] stringList = null;

    public java.util.Map<Integer, TestReq> mapData = null;

    public int getId()
    {
        return id;
    }

    public void  setId(int id)
    {
        this.id = id;
    }

    public String getCode()
    {
        return code;
    }

    public void  setCode(String code)
    {
        this.code = code;
    }

    public String[] getStringList()
    {
        return stringList;
    }

    public void  setStringList(String[] stringList)
    {
        this.stringList = stringList;
    }

    public java.util.Map<Integer, TestReq> getMapData()
    {
        return mapData;
    }

    public void  setMapData(java.util.Map<Integer, TestReq> mapData)
    {
        this.mapData = mapData;
    }

    public TestData()
    {
    }

    public TestData(int id, String code, String[] stringList, java.util.Map<Integer, TestReq> mapData)
    {
        this.id = id;
        this.code = code;
        this.stringList = stringList;
        this.mapData = mapData;
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        TestData t = (TestData) o;
        return (
            com.qq.tars.codec.TarsUtil.equals(id, t.id) && 
            com.qq.tars.codec.TarsUtil.equals(code, t.code) && 
            com.qq.tars.codec.TarsUtil.equals(stringList, t.stringList) && 
            com.qq.tars.codec.TarsUtil.equals(mapData, t.mapData) );
    }

    public int hashCode()
    {
        try
        {
            throw new Exception("Need define key first!");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return 0;
    }
    public Object clone()
    {
        Object o = null;
        try
        {
            o = super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return o;
    }

    public void writeTo(com.qq.tars.codec.TarsOutputStream _os)
    {
        _os.write(id, 0);
        if (null != code)
        {
            _os.write(code, 1);
        }
        if (null != stringList)
        {
            _os.write(stringList, 2);
        }
        if (null != mapData)
        {
            _os.write(mapData, 3);
        }
    }

    static String[] cache_stringList;
    static {
        cache_stringList = (String[]) new String[1];
        String __var_72 = "";
        ((String[])cache_stringList)[0] = __var_72;
    }
    static java.util.Map<Integer, TestReq> cache_mapData;
    static {
        cache_mapData = new java.util.HashMap<Integer, TestReq>();
        Integer __var_73 = 0;
        TestReq __var_74 = new TestReq();
        cache_mapData.put(__var_73, __var_74);
    }

    public void readFrom(com.qq.tars.codec.TarsInputStream _is)
    {
        this.id = (int) _is.read(id, 0, false);
        this.code =  _is.readString(1, false);
        this.stringList = (String[]) _is.read(cache_stringList, 2, false);
        this.mapData = (java.util.Map<Integer, TestReq>) _is.read(cache_mapData, 3, false);
    }

    public void display(StringBuilder _os, int _level)
    {
        com.qq.tars.codec.TarsDisplayer _ds = new com.qq.tars.codec.TarsDisplayer(_os, _level);
        _ds.display(id, "id");
        _ds.display(code, "code");
        _ds.display(stringList, "stringList");
        _ds.display(mapData, "mapData");
    }

}


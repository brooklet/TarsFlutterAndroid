// **********************************************************************
// This file was generated by a TARS parser!
// TARS version 2.4.14.
// **********************************************************************

package com.qq.tars.example.idl;

public final class TestReq extends com.qq.tars.codec.TarsStruct implements Cloneable
{
    public String className()
    {
        return "micang.TestReq";
    }

    public String fullClassName()
    {
        return "TestReq";
    }

    public int id = 0;

    public String name = "";


    public int getId()
    {
        return id;
    }

    public void  setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void  setName(String name)
    {
        this.name = name;
    }

    public TestReq()
    {
    }

    public TestReq(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }

        TestReq t = (TestReq) o;
        return (
            com.qq.tars.codec.TarsUtil.equals(id, t.id) && 
            com.qq.tars.codec.TarsUtil.equals(name, t.name));
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
        if (null != name)
        {
            _os.write(name, 1);
        }

    }


    public void readFrom(com.qq.tars.codec.TarsInputStream _is)
    {
        this.id = (int) _is.read(id, 0, false);
        this.name =  _is.readString(1, false);
    }

    public void display(StringBuilder _os, int _level)
    {
        com.qq.tars.codec.TarsDisplayer _ds = new com.qq.tars.codec.TarsDisplayer(_os, _level);
        _ds.display(id, "id");
        _ds.display(name, "name");
    }

}


package com.mm.gui.table.experiment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created using IntelliJ IDEA. Date: Apr 2, 2005 Time: 4:03:55 PM
 *
 * @author Miguel Mu\u00f1oz
 *         <p/>
 *         Copyright (c) 2004 Miguel Munoz
 */

public class GenTest<Z>
{
	
	private GenTest()
	{
		List<A> alist = new ArrayList<A>();
		List<? extends A> qalist = new ArrayList<A>();
		List<? extends A> blist  = new ArrayList<B>();
//		alist.add(qalist);  // Can't be done, because qalist (A List) doesn't extend A
//		alist.add(blist);   // Can't be done, because blist (A List) doesn't extend A
		A a = new A();
		B b = new B();
		C c = new C();
		alist.add(a);
		alist.add(b);
		alist.add(c);
//		blist.add(c);   // not allowed because we can't add c to blist.
//		qalist.add(a);  //    (That's why we can't add ANYTHING to blist. )
//		qalist.add(b);  //    (We don't know the actual type of the list, and so )
//		blist.add(a);   //    (we can't guarantee any object is of right type.)
//		blist.add(b);
	}
}

class A { }

class B extends A { }

class C extends A { } 

//class Z { }
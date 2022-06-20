/*
 * Corso di Programmazione Orientata agli Oggetti - Progetto LinkedList<T>
 * Autore: De Marco Alessandro (Numero Matricola: 190020)
 * Data: 01/2019
 */

package llp;
import java.util.Iterator;

public abstract class AbstractList<T> implements List<T> {
	private static final long serialVersionUID = -3898877475936258143L;
	public String toString() {
		StringBuilder sb=new StringBuilder(500);
		sb.append('[');
		Iterator<T> it=iterator();
		while( it.hasNext() ) {
			sb.append(it.next());
			if( it.hasNext() )
				sb.append(", ");
		}
		sb.append(']');
		return sb.toString();
	}
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if( !(o instanceof List) ) return false;
		if( o==this ) return true;
		List<T> l=(List<T>) o;
		if( size()!=l.size() ) return false;
		Iterator<T> it1=iterator();
		Iterator<T> it2=l.iterator();
		while( it1.hasNext() )
			if( !it1.next().equals(it2.next()) )
				return false;
		return true;
	}
	public int hashCode() { //hashCode implementation from the book "Effective Java (Chapter 3)"
		int res=1;
		Iterator<T> it=iterator();
		while( it.hasNext() ) {
			T f=it.next();
			int c=0;
			if( f instanceof Boolean ) c=(boolean)f ? 0 : 1;
			else if( f instanceof Byte ) c=(byte)f;
			else if( f instanceof Character ) c=(char)f;
			else if( f instanceof Short ) c=(short)f;
			else if( f instanceof Integer ) c=(int)f;
			else if( f instanceof Long ) {
				long l=(long)f;
				c=(int)(l^(l >>> 32)); //Bitwise operators: ^ (XOR operator), >>> (zero fill right shift operator)
			}
			else if( f instanceof Float ) c=Float.floatToIntBits((float)f);
			else if( f instanceof Double ) {
				long l=Double.doubleToLongBits((double)f);
				c=(int)(l^(l >>> 32));
			}
			else 
				if( f!=null ) c=f.hashCode();
			res=37*res+c;
		}
		return res;
	}
}

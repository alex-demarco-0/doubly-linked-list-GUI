/*
 * Corso di Programmazione Orientata agli Oggetti - Progetto LinkedList<T>
 * Autore: De Marco Alessandro (Numero Matricola: 190020)
 * Data: 01/2019
 */

package llp;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public interface List<T> extends Iterable<T>, Serializable {
	default int size() {
		int c=0;
		for(Iterator<T> it=iterator(); it.hasNext(); it.next(), c++);
		return c;
	}
	default boolean contains( T x ) {
		Iterator<T> it=iterator();
		while( it.hasNext() )
			if( it.next().equals(x) )
				return true;
		return false;
	}
	default void clear() {
		for(Iterator<T> it=iterator(); it.hasNext(); it.next(), it.remove());
	}
	default void add( T x ) {
		ListIterator<T> lit=listIterator();
		while( lit.hasNext() )
			lit.next();
		lit.add(x);
	}
	default void addFirst( T x ) {
		listIterator().add(x);
	}
	default void addLast( T x ) {
		ListIterator<T> lit=listIterator();
		while( lit.hasNext() )
			lit.next();
		lit.add(x);
	}
	default T getFirst() {
		Iterator<T> it=iterator();
		if( !it.hasNext() ) throw new NoSuchElementException();
		return it.next();
	}
	default T getLast() {
		ListIterator<T> lit=listIterator();
		if( !lit.hasNext() ) throw new NoSuchElementException();
		while( lit.hasNext() )
			lit.next();
		return lit.previous();
	}
	default T removeFirst() {
		Iterator<T> it=iterator();
		if( !it.hasNext() ) throw new NoSuchElementException();
		T x=it.next();
		it.remove();
		return x;
	}
	default T removeLast() {
		ListIterator<T> lit=listIterator();
		if( !lit.hasNext() ) throw new NoSuchElementException();
		while( lit.hasNext() )
			lit.next();
		T x=lit.previous();
		lit.remove();
		return x;
	}
	default void remove( T x ) {
		Iterator<T> it=iterator();
		while( it.hasNext() )
			if( it.next().equals(x) ) {
				it.remove();
				break;
			}
	}
	default boolean isEmpty() {
		return !iterator().hasNext();
	}
	default boolean isFull() {
		return false;
	}
	static <T> void sort( List<T> l, Comparator<T> c ) {
		ListIterator<T> lit=l.listIterator();
		if( !lit.hasNext() ) return;
		lit.next();
		if( !lit.hasNext() ) return;
		boolean switches=true;
		int pos=1, limit=l.size(), lsp=0;
		while( switches ) {
			T cur=lit.next();
			lit.previous();
			switches=false;
			while( pos<limit ) {
				T pre=null;
				if( c.compare(cur, pre=lit.previous())<0 ) {
					lit.set(cur);
					lit.next();
					lit.next();
					lit.set(pre);
					lsp=pos;
					switches=true;
				}
				else {
					lit.next();
					lit.next();
				}
				if( !lit.hasNext() )
					cur=null;
				else {
					cur=lit.next();
					lit.previous();
				}
				pos++;
			}
			limit=lsp;
			while( lit.hasPrevious() ) lit.previous();
			lit.next(); pos=1;
		}
	}
	ListIterator<T> listIterator();
	ListIterator<T> listIterator( int from );
}

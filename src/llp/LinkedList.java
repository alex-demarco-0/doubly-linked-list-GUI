/*
 * Corso di Programmazione Orientata agli Oggetti - Progetto LinkedList<T>
 * Autore: De Marco Alessandro (Numero Matricola: 190020)
 * Data: 01/2019
 */

package llp;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

public class LinkedList<T> extends AbstractList<T> {

	private static class Nodo<E> implements Serializable {
		private static final long serialVersionUID = -2256854214941092539L;
		E info;
		Nodo<E> next, prior;
	}

	private static final long serialVersionUID = -506403566987601779L;
	private enum Move { UNKNOWN, FORWARD, BACKWARDS }
	private int count=0;
	private Nodo<T> first=null, last=null;
	private int size=0;

	public int size() {
		return size;
	}

	public boolean contains(T x) {
		for(Nodo<T> cor=first; cor!=null; cor=cor.next)
			if( cor.info.equals(x) )
				return true;
		return false;
	}

	public void clear() {
		first=null; last=null; size=0;
		count++;
	}

	public void add(T x) {
		addLast(x); 
	}

	public void addFirst(T x) {
		Nodo<T> n=new Nodo<>();
		n.info=x; n.next=first; n.prior=null;
		if( first!=null )
			first.prior=n;
		first=n;
		if( last==null )
			last=n;
		size++;
		count++;
	}

	public void addLast(T x) {
		Nodo<T> n=new Nodo<>();
		n.info=x; n.next=null; n.prior=last;
		if( last!=null )
			last.next=n;
		last=n;
		if( first==null )
			first=n;
		size++;
		count++;
	}

	public T getFirst() {
		if( first!=null )
			return first.info;
		return null;
	}

	public T getLast() {
		if( last!=null )
			return last.info;
		return null;
	}

	public T removeFirst() {
		if( first==null ) throw new NoSuchElementException();
		T f=first.info;
		first=first.next;
		if( first==null )
			last=null;
		else first.prior=null;
		size--;
		count++;
		return f;
	}

	public T removeLast() {
		if( last==null ) throw new NoSuchElementException();
		T l=last.info;
		last=last.prior;
		if( last==null )
			first=null;
		else last.next=null;
		size--;
		count++;
		return l;
	}

	public void remove(T x) {
		Nodo<T> cor=first;
		while( cor!=null && !cor.info.equals(x) )
			cor=cor.next;
		if( cor!=null ) {
			if( cor==first ) {
				first=first.next;
				if( first==null )
					last=null;
				else first.prior=null;
			}
			else if( cor==last ) {
				last=last.prior;
				last.next=null;
			}
			else {
				cor.prior.next=cor.next;
				cor.next.prior=cor.prior;
			}
			size--;
			count++;
		}
	}

	public boolean isEmpty() {
		return first==null;
	}
/*
	public void sort(Comparator<T> c) {
		if( first==null || first.next==null ) return;
		boolean scambi=true;
		Nodo<T> limite=null, rus=null;
		while( scambi ) {
			Nodo<T> cor=first.next;
			scambi=false;
			while( cor!=limite ) {
				if( c.compare(cor.info,cor.prior.info)<0 ) {
					T park=cor.info;
					cor.info=cor.prior.info;
					cor.prior.info=park;
					rus=cor;
					scambi=true;
				}
				cor=cor.next;
			}
			limite=rus;
		}
	}

	public String toString() {
		StringBuilder sb=new StringBuilder(500);
		sb.append('[');
		Nodo<T> cor=first;
		while( cor!=null ) {
			sb.append(cor.info);
			cor=cor.next;
			if( cor!=null )
				sb.append(", ");
		}
		sb.append(']');
		return sb.toString();
	}
*/
	public Iterator<T> iterator() {
		return new ListIteratorImpl();
	}

	public ListIterator<T> listIterator() {
		return new ListIteratorImpl();
	}

	public ListIterator<T> listIterator(int from) {
		return new ListIteratorImpl(from);
	}

	private class ListIteratorImpl implements ListIterator<T> {
		private Nodo<T> previous, next;
		private Move lastMove=Move.UNKNOWN;
		private int itCount=count;
		public ListIteratorImpl() {
			previous=null; next=first;
		}
		public ListIteratorImpl(int from) {
			if( from<0 || from>size ) throw new IllegalArgumentException();
			if( from==size ) {
				previous=last; next=null;
			}
			else {
				previous=null; next=first;
				for(int i=0; i<from; ++i) {
					previous=next; next=next.next;
				}
			}
		}
		public boolean hasNext() {
			return next!=null;
		}
		public T next() {
			if( count!=itCount ) throw new ConcurrentModificationException();
			if( !hasNext() ) throw new NoSuchElementException();
			lastMove=Move.FORWARD;
			previous=next;
			next=next.next;
			return previous.info;
		}
		public boolean hasPrevious() {
			return previous!=null;
		}
		public T previous() {
			if( count!=itCount ) throw new ConcurrentModificationException();
			if( !hasPrevious() ) throw new NoSuchElementException();
			lastMove=Move.BACKWARDS;
			next=previous;
			previous=previous.prior;
			return next.info;
		}
		public void remove() {
			if( count!=itCount ) throw new ConcurrentModificationException();
			if( lastMove==Move.UNKNOWN ) throw new IllegalStateException();
			Nodo<T> r=null;
			if( lastMove==Move.FORWARD )
				r=previous;
			else
				r=next;
			if( r==first ) {
				first=first.next;
				if( first==null )
					last=null;
				else first.prior=null;
			}
			else if( r==last ) {
				last=last.prior;
				last.next=null;
			}
			else {
				r.prior.next=r.next;
				r.next.prior=r.prior;
			}
			if( lastMove==Move.FORWARD )
				previous=r.prior;
			else
				next=r.next;
			size--;
			lastMove=Move.UNKNOWN;
			count++;
			itCount++;
		}
		public void add( T elem ) {
			if( count!=itCount ) throw new ConcurrentModificationException();
			Nodo<T> a=new Nodo<>();
			a.info=elem;
			a.next=next;
			a.prior=previous;
			if( previous==null )
				first=a;
			else
				previous.next=a;
			if( next==null )
				last=a;
			else
				next.prior=a;
			previous=a;
			size++;
			lastMove=Move.UNKNOWN;
			count++;
			itCount++;
		}
		public void set( T elem ) {
			if( lastMove==Move.UNKNOWN ) throw new IllegalStateException();
			if( count!=itCount ) throw new ConcurrentModificationException();
			if( lastMove==Move.FORWARD )
				previous.info=elem;
			else next.info=elem;
		}
		public int nextIndex() {
			throw new UnsupportedOperationException();
		}
		public int previousIndex() {
			throw new UnsupportedOperationException();
		}
	}
}

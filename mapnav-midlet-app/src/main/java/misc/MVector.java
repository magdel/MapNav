/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package misc;

import java.util.NoSuchElementException;

/**
 *
 * @author rfk
 */
public class MVector {
 /**
053:             * The array buffer into which the components of the vector are
054:             * stored. The capacity of the vector is the length of this array buffer.
055:             *
056:             * @since   JDK1.0
057:             */
            protected Object elementData[];
           /**
061:             * The number of valid components in the vector.
062:             *
063:             * @since   JDK1.0
064:             */
           protected int elementCount;
          /**
068:             * The amount by which the capacity of the vector is automatically
069:             * incremented when its size becomes greater than its capacity. If
070:             * the capacity increment is <code>0</code>, the capacity of the
071:             * vector is doubled each time it needs to grow.
072:             *
073:             * @since   JDK1.0
074:             */
            protected int capacityIncrement;
             /**
078:             * Constructs an empty vector with the specified initial capacity and
079:             * capacity increment.
080:             *
081:             * @param   initialCapacity     the initial capacity of the vector.
082:             * @param   capacityIncrement   the amount by which the capacity is
083:             *                              increased when the vector overflows.
084:             * @exception IllegalArgumentException if the specified initial capacity
085:             *            is negative
086:             */
            public MVector(int initialCapacity, int capacityIncrement) {
                super ();
                if (initialCapacity < 0) {
                    throw new IllegalArgumentException(
                    /* #ifdef VERBOSE_EXCEPTIONS */
                    /// skipped                       "Illegal Capacity: "+ initialCapacity
                    /* #endif */
                    );
                }
                this .elementData = new Object[initialCapacity];
                this .capacityIncrement = capacityIncrement;
            }
/**
101:             * Constructs an empty vector with the specified initial capacity.
102:             *
103:             * @param   initialCapacity   the initial capacity of the vector.
104:             * @since   JDK1.0
105:             */
           public MVector(int initialCapacity) {
               this (initialCapacity, 0);
           }

           /**
111:             * Constructs an empty vector.
112:             *
113:             * @since   JDK1.0
114:             */
           public MVector() {
               this (10);
          }
/**
151:             * Increases the capacity of this vector, if necessary, to ensure
152:             * that it can hold at least the number of components specified by
153:             * the minimum capacity argument.
154:             *
155:             * @param   minCapacity   the desired minimum capacity.
156:             * @since   JDK1.0
157:             */
           public void ensureCapacity(int minCapacity) {
               if (minCapacity > elementData.length) {
                   ensureCapacityHelper(minCapacity);
                }
            }

/**
165:             * This implements the unsynchronized semantics of ensureCapacity.
166:             * Synchronized methods in this class can internally call this
167:             * method for ensuring capacity without incurring the cost of an
168:             * extra synchronization.
169:             *
170:             * @see java.util.Vector#ensureCapacity(int)
171:             */
            private void ensureCapacityHelper(int minCapacity) {
                int oldCapacity = elementData.length;
               Object oldData[] = elementData;
               int newCapacity = (capacityIncrement > 0) ? (oldCapacity + capacityIncrement)
                       : (oldCapacity * 2);
                if (newCapacity < minCapacity) {
                   newCapacity = minCapacity;
               }
              elementData = new Object[newCapacity];
               System.arraycopy(oldData, 0, elementData, 0,
                       elementCount);
            }


            /**
186:             * Sets the size of this vector. If the new size is greater than the
187:             * current size, new <code>null</code> items are added to the end of
188:             * the vector. If the new size is less than the current size, all
189:             * components at index <code>newSize</code> and greater are discarded.
190:             *
191:             * @param   newSize   the new size of this vector.
192:             * @throws  ArrayIndexOutOfBoundsException if new size is negative.
193:             * @since   JDK1.0
194:             */
            public void setSize(int newSize) {
                if ((newSize > elementCount) && (newSize > elementData.length)) {
                    ensureCapacityHelper(newSize);
                } else {
                    for (int i = newSize; i < elementCount; i++) {
                        elementData[i] = null;
                    }
                }
                elementCount = newSize;
            }
            /**
207:             * Returns the current capacity of this vector.
208:             *
209:             * @return  the current capacity of this vector.
210:             * @since   JDK1.0
211:             */
            public int capacity() {
                return elementData.length;
            }

            /**
217:             * Returns the number of components in this vector.
218:             *
219:             * @return  the number of components in this vector.
220:             * @since   JDK1.0
221:             */
            public int size() {
                return elementCount;
            }

            /**
227:             * Tests if this vector has no components.
228:             *
229:             * @return  <code>true</code> if this vector has no components;
230:             *          <code>false</code> otherwise.
231:             * @since   JDK1.0
232:             */
            public boolean isEmpty() {
                return elementCount == 0;
            }
            /**
249:             * Tests if the specified object is a component in this vector.
250:             *
251:             * @param   elem   an object.
252:             * @return  <code>true</code> if the specified object is a component in
253:             *          this vector; <code>false</code> otherwise.
254:             * @since   JDK1.0
255:             */
            public boolean contains(Object elem) {
                return indexOf(elem, 0) >= 0;
            }

            /**
261:             * Searches for the first occurrence of the given argument, testing
262:             * for equality using the <code>equals</code> method.
263:             *
264:             * @param   elem   an object.
265:             * @return  the index of the first occurrence of the argument in this
266:             *          vector; returns <code>-1</code> if the object is not found.
267:             * @see     java.lang.Object#equals(java.lang.Object)
268:             * @since   JDK1.0
269:             */
            public int indexOf(Object elem) {
                return indexOf(elem, 0);
            }

            /**
275:             * Searches for the first occurrence of the given argument, beginning
276:             * the search at <code>index</code>, and testing for equality using
277:             * the <code>equals</code> method.
278:             *
279:             * @param   elem    an object.
280:             * @param   index   the index to start searching from.
281:             * @return  the index of the first occurrence of the object argument in
282:             *          this vector at position <code>index</code> or later in the
283:             *          vector; returns <code>-1</code> if the object is not found.
284:             * @see     java.lang.Object#equals(java.lang.Object)
285:             * @since   JDK1.0
286:             */
            public int indexOf(Object elem, int index) {
                if (elem == null) {
                    for (int i = index; i < elementCount; i++)
                        if (elementData[i] == null)
                            return i;
                } else {
                    for (int i = index; i < elementCount; i++)
                        if (elem.equals(elementData[i]))
                            return i;
                }
                return -1;
            }

            /**
301:             * Returns the index of the last occurrence of the specified object in
302:             * this vector.
303:             *
304:             * @param   elem   the desired component.
305:             * @return  the index of the last occurrence of the specified object in
306:             *          this vector; returns <code>-1</code> if the object is not found.
307:             * @since   JDK1.0
308:             */
            public int lastIndexOf(Object elem) {
                return lastIndexOf(elem, elementCount - 1);
            }

            /**
314:             * Searches backwards for the specified object, starting from the
315:             * specified index, and returns an index to it.
316:             *
317:             * @param   elem    the desired component.
318:             * @param   index   the index to start searching from.
319:             * @return  the index of the last occurrence of the specified object in this
320:             *          vector at position less than <code>index</code> in the vector;
321:             *          <code>-1</code> if the object is not found.
322:             * @exception  IndexOutOfBoundsException  if <tt>index</tt> is greater
323:             *             than or equal to the current size of this vector.
324:             * @since   JDK1.0
325:             */
            public int lastIndexOf(Object elem, int index) {
                if (index >= elementCount) {
                    throw new IndexOutOfBoundsException(
                    /* #ifdef VERBOSE_EXCEPTIONS */
                    /// skipped                       index + " >= " + elementCount
                    /* #endif */
                    );
                }

                if (elem == null) {
                    for (int i = index; i >= 0; i--)
                        if (elementData[i] == null)
                            return i;
                } else {
                    for (int i = index; i >= 0; i--)
                        if (elem.equals(elementData[i]))
                            return i;
                }
                return -1;
            }

            /**
348:             * Returns the component at the specified index.
349:             *
350:             * @param      index   an index into this vector.
351:             * @return     the component at the specified index.
352:             * @exception  ArrayIndexOutOfBoundsException  if an invalid index was
353:             *             given.
354:             * @since      JDK1.0
355:             */
            public Object elementAt(int index) {
                if (index >= elementCount) {
                    throw new ArrayIndexOutOfBoundsException(
                    /* #ifdef VERBOSE_EXCEPTIONS */
                    /// skipped                       index + " >= " + elementCount
                    /* #endif */
                    );
                }
                return elementData[index];
            }

            /**
368:             * Returns the first component of this vector.
369:             *
370:             * @return     the first component of this vector.
371:             * @exception  NoSuchElementException  if this vector has no components.
372:             * @since      JDK1.0
373:             */
            public Object firstElement() {
                if (elementCount == 0) {
                    throw new NoSuchElementException();
                }
                return elementData[0];
            }

            /**
382:             * Returns the last component of the vector.
383:             *
384:             * @return  the last component of the vector, i.e., the component at index
385:             *          <code>size()&nbsp;-&nbsp;1</code>.
386:             * @exception  NoSuchElementException  if this vector is empty.
387:             * @since   JDK1.0
388:             */
           public Object lastElement() {
                if (elementCount == 0) {
                    throw new NoSuchElementException();
                }
                return elementData[elementCount - 1];
            }

            /**
397:             * Sets the component at the specified <code>index</code> of this
398:             * vector to be the specified object. The previous component at that
399:             * position is discarded.
400:             * <p>
401:             * The index must be a value greater than or equal to <code>0</code>
402:             * and less than the current size of the vector.
403:             *
404:             * @param      obj     what the component is to be set to.
405:             * @param      index   the specified index.
406:             * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
407:             * @see        java.util.Vector#size()
408:             * @since      JDK1.0
409:             */
            public void setElementAt(Object obj, int index) {
                if (index >= elementCount) {
                    throw new ArrayIndexOutOfBoundsException(
                    /* #ifdef VERBOSE_EXCEPTIONS */
                    /// skipped                       index + " >= " +
                    /// skipped                       elementCount
                    /* #endif */
                    );
                }
                elementData[index] = obj;
            }

            /**
423:             * Deletes the component at the specified index. Each component in
424:             * this vector with an index greater or equal to the specified
425:             * <code>index</code> is shifted downward to have an index one
426:             * smaller than the value it had previously.
427:             * <p>
428:             * The index must be a value greater than or equal to <code>0</code>
429:             * and less than the current size of the vector.
430:             *
431:             * @param      index   the index of the object to remove.
432:             * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
433:             * @see        java.util.Vector#size()
434:             * @since      JDK1.0
435:             */
            public void removeElementAt(int index) {
                if (index >= elementCount) {
                    throw new ArrayIndexOutOfBoundsException(
                    /* #ifdef VERBOSE_EXCEPTIONS */
                    /// skipped                       index + " >= " +
                    /// skipped                       elementCount
                    /* #endif */
                    );
                } else if (index < 0) {
                    throw new ArrayIndexOutOfBoundsException(
                    /* #ifdef VERBOSE_EXCEPTIONS */
                    /// skipped                       index
                    /* #endif */
                    );
                }
                int j = elementCount - index - 1;
                if (j > 0) {
                    System.arraycopy(elementData, index + 1,
                            elementData, index, j);
                }
                elementCount--;
                elementData[elementCount] = null; /* to let gc do its work */
            }

            /**
461:             * Inserts the specified object as a component in this vector at the
462:             * specified <code>index</code>. Each component in this vector with
463:             * an index greater or equal to the specified <code>index</code> is
464:             * shifted upward to have an index one greater than the value it had
465:             * previously.
466:             * <p>
467:             * The index must be a value greater than or equal to <code>0</code>
468:             * and less than or equal to the current size of the vector.
469:             *
470:             * @param      obj     the component to insert.
471:             * @param      index   where to insert the new component.
472:             * @exception  ArrayIndexOutOfBoundsException  if the index was invalid.
473:             * @see        java.util.Vector#size()
474:             * @since      JDK1.0
475:             */
            public void insertElementAt(Object obj, int index) {
                int newcount = elementCount + 1;
                if (index < 0 || index >= newcount) {
                    throw new ArrayIndexOutOfBoundsException(
                    /* #ifdef VERBOSE_EXCEPTIONS */
                    /// skipped                       index + " > " + elementCount
                    /* #endif */
                    );
                }
                if (newcount > elementData.length) {
                    ensureCapacityHelper(newcount);
                }
                System.arraycopy(elementData, index, elementData,
                        index + 1, elementCount - index);
                elementData[index] = obj;
                elementCount++;
            }

            /**
495:             * Adds the specified component to the end of this vector,
496:             * increasing its size by one. The capacity of this vector is
497:             * increased if its size becomes greater than its capacity.
498:             *
499:             * @param   obj   the component to be added.
500:             * @since   JDK1.0
501:             */
            public void addElement(Object obj) {
                int newcount = elementCount + 1;
                if (newcount > elementData.length) {
                    ensureCapacityHelper(newcount);
                }
                elementData[elementCount++] = obj;
            }

            /**
511:             * Removes the first occurrence of the argument from this vector. If
512:             * the object is found in this vector, each component in the vector
513:             * with an index greater or equal to the object's index is shifted
514:             * downward to have an index one smaller than the value it had previously.
515:             *
516:             * @param   obj   the component to be removed.
517:             * @return  <code>true</code> if the argument was a component of this
518:             *          vector; <code>false</code> otherwise.
519:             * @since   JDK1.0
520:             */
            public boolean removeElement(Object obj) {
                int i = indexOf(obj);
                if (i >= 0) {
                    removeElementAt(i);
                    return true;
                }
                return false;
            }

            /**
531:             * Removes all components from this vector and sets its size to zero.
532:             *
533:             * @since   JDK1.0
534:             */
            public void removeAllElements() {
                for (int i = 0; i < elementCount; i++) {
                    elementData[i] = null;
                }
                elementCount = 0;
            }


}

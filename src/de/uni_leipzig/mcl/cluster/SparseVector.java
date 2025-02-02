/*
 * Copyright (c) 2006 Gregor Heinrich. All rights reserved. Redistribution and
 * use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met: 1. Redistributions of source
 * code must retain the above copyright notice, this list of conditions and the
 * following disclaimer. 2. Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the distribution.
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESSED OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/*
 * Created on 03.08.2006
 */
package de.uni_leipzig.mcl.cluster;

import java.util.HashMap;
import java.util.Iterator;

/**
 * SparseVector represents a sparse vector.
 * <p>
 * Conventions: except for the inherited methods and normalise(double),
 * operations leave <tt>this</tt> ummodified (immutable) if there is a return
 * value. Within operations, no pruning of values close to zero is done. Pruning
 * can be controlled via the prune() method.
 *
 * @author gregor :: arbylon . net
 */
public class SparseVector extends HashMap<Integer, Double> {

    private static final long serialVersionUID = 1L;

    private int length = 0;

    /**
     * create empty vector
     */
    public SparseVector() {
        super();
    }

    /**
     * create empty vector with length
     */
    public SparseVector(int i) {
        this();
        length = i;
    }

    /**
     * create vector from dense vector
     *
     * @param x
     */
    public SparseVector(double[] x) {
        this(x.length);
        for (int i = 0; i < x.length; i++) {
            if (x[i] != 0) {
                put(i, x[i]);
            }
        }
    }

    /**
     * copy constructor
     *
     * @param v
     */
    public SparseVector(SparseVector v) {
        super(v);
        this.length = v.length;
    }

    /**
     * get ensures it returns 0 for empty hash values or if index exceeds
     * length.
     *
     * @param key
     * @return val
     */
    @Override
    public Double get(Object key) {
        Double b = super.get(key);
        if (b == null) {
            return 0.;
        }
        return b;
    }

    /**
     * put increases the matrix size if the index exceeds the current size.
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public Double put(Integer key, Double value) {
        length = Math.max(length, key + 1);
        if (value == 0) {
            return remove(key);
        }
        return super.put(key, value);
    }

    /**
     * normalises the vector to 1.
     */
    public void normalise() {
        double invsum = 1. / sum();
        for (int i : keySet()) {
            mult(i, invsum);
        }
    }

    /**
     * normalises the vector to newsum
     *
     * @param the value to which the element sum
     * @return the old element sum
     */
    public double normalise(double newsum) {
        double sum = sum();
        double invsum = newsum / sum;
        for (int i : keySet()) {
            mult(i, invsum);
        }
        return sum;
    }

    /**
     * sum of the elements
     *
     * @return
     */
    private double sum() {
        double sum = 0;
        for (double a : values()) {
            sum += a;
        }
        return sum;
    }

    /**
     * power sum of the elements
     *
     * @return
     */
    public double sum(double s) {
        double sum = 0;
        for (double a : values()) {
            sum += Math.pow(a, s);
        }
        return sum;
    }

    /**
     * mutable add
     *
     * @param v
     */
    public void add(SparseVector v) {
        for (int i : keySet()) {
            add(i, v.get(i));
        }
    }

    /**
     * mutable mult
     *
     * @param i index
     * @param a value
     */
    public void mult(int i, double a) {
        Double c = get(i);
        c *= a;
        put(i, c);
    }

        /**
     * mutable mult
     *
     * @param i index
     * @param a value
     */
    public void setSignum(int i, double a) {        
        Double c = get(i);
        if(a<=0) c = new Double((-1)*java.lang.Math.abs(c.doubleValue()));
        else c = new Double(java.lang.Math.abs(c.doubleValue()));
        put(i, c);
    }
    /**
     * mutable factorisation
     *
     * @param a
     */
    public void factor(double a) {
        SparseVector s = copy();
        for (int i : keySet()) {
            s.mult(i, a);
        }
    }

    /**
     * Change signum
     *
     * @param a
     */
    public SparseVector setSignum(double a) {        
        SparseVector s = copy();
        for (int i : keySet()) {
            s.setSignum(i, a);
        }
       return s;
    }
    /**
     * immutable scalar product
     *
     * @param v
     * @return scalar product
     */
    public double times(SparseVector v) {
        double sum = 0;
        for (int i : keySet()) {
            sum += get(i) * v.get(i);
        }
        return sum;
    }

    /**
     * mutable Hadamard product (elementwise multiplication)
     *
     * @param v
     */
    public void hadamardProduct(SparseVector v) {
        for (int i : keySet()) {
            put(i, v.get(i) * get(i));
        }
    }

    /**
     * mutable Hadamard power
     *
     * @param s
     */
    public void hadamardPower(double s) {
        if(keySet()!=null)
        for (int i : keySet()) {
            put(i, Math.pow(get(i), s));
        }
    }

    /**
     * mutable add
     *
     * @param i
     * @param a
     */
    public void add(int i, double a) {
        length = Math.max(length, i + 1);
        double c = get(i);
        c += a;
        put(i, c);
    }

    /**
     * get the length of the vector
     *
     * @return
     */
    public final int getLength() {
        return length;
    }

    /**
     * set the new length of the vector (regardless of the maximum index).
     *
     * @param length
     */
    public final void setLength(int length) {
        this.length = length;
    }

    /**
     * copy the contents of the sparse vector
     *
     * @return
     */
    public SparseVector copy() {
        return new SparseVector(this);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i : keySet()) {
            sb.append(i).append("->").append(get(i)).append(", ");
        }
        return sb.toString();
    }

    /**
     * create string representation of dense equivalent.
     *
     * @return
     */
//    public String toStringDense() {
//        return Vectors.print(getDense());
//    }

    /**
     * get dense represenation
     *
     * @return
     */
    public double[] getDense() {
        double[] a = new double[length];
        for (int i : keySet()) {
            a[i] = get(i);
        }
        return a;
    }

    /**
     * maximum element value
     *
     * @return
     */
    public double max() {
        double max = 0;
        for (int i : keySet()) {
            max = Math.max(get(i), max);
        }
        return max;
    }

    /**
     * exponential sum, i.e., sum (elements^p)
     *
     * @param p
     * @return
     */
    public double expSum(int p) {
        double sum = 0;
        for (double a : values()) {
            sum += Math.pow(a, p);
        }
        return sum;
    }

    /**
     * remove all elements whose magnitude is < threshold
     *
     * @param threshold
     */
    public void prune(double threshold) {
        for (Iterator<Integer> it = keySet().iterator(); it.hasNext();) {
            int key = it.next();
            if (Math.abs(get(key)) < threshold) {
                it.remove();
            }
        }
    }
}

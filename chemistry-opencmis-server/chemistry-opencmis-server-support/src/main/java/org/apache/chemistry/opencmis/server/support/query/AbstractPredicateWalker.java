/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contributors:
 *     Florent Guillaume, Nuxeo
 */
package org.apache.chemistry.opencmis.server.support.query;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.tree.Tree;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;

/**
 * Basic implementation walking a predicate in lexical order.
 * <p>
 * The {@code walkXYZ} methods can be overridden to change the walking order.
 */
public abstract class AbstractPredicateWalker implements PredicateWalker {

    public boolean walkPredicate(Tree node) {
        switch (node.getType()) {
        case CmisQlStrictLexer.NOT:
            return walkNot(node, node.getChild(0));
        case CmisQlStrictLexer.AND:
            return walkAnd(node, node.getChild(0), node.getChild(1));
        case CmisQlStrictLexer.OR:
            return walkOr(node, node.getChild(0), node.getChild(1));
        case CmisQlStrictLexer.EQ:
            return walkEquals(node, node.getChild(0), node.getChild(1));
        case CmisQlStrictLexer.NEQ:
            return walkNotEquals(node, node.getChild(0), node.getChild(1));
        case CmisQlStrictLexer.GT:
            return walkGreaterThan(node, node.getChild(0), node.getChild(1));
        case CmisQlStrictLexer.GTEQ:
            return walkGreaterOrEquals(node, node.getChild(0), node.getChild(1));
        case CmisQlStrictLexer.LT:
            return walkLessThan(node, node.getChild(0), node.getChild(1));
        case CmisQlStrictLexer.LTEQ:
            return walkLessOrEquals(node, node.getChild(0), node.getChild(1));
        case CmisQlStrictLexer.IN:
            return walkIn(node, node.getChild(0), node.getChild(1));
        case CmisQlStrictLexer.NOT_IN:
            return walkNotIn(node, node.getChild(0), node.getChild(1));
        case CmisQlStrictLexer.IN_ANY:
            return walkInAny(node, node.getChild(0), node.getChild(1));
        case CmisQlStrictLexer.NOT_IN_ANY:
            return walkNotInAny(node, node.getChild(0), node.getChild(1));
        case CmisQlStrictLexer.EQ_ANY:
            return walkEqAny(node, node.getChild(0), node.getChild(1));
        case CmisQlStrictLexer.IS_NULL:
            return walkIsNull(node, node.getChild(0));
        case CmisQlStrictLexer.IS_NOT_NULL:
            return walkIsNotNull(node, node.getChild(0));
        case CmisQlStrictLexer.LIKE:
            return walkLike(node, node.getChild(0), node.getChild(1));
        case CmisQlStrictLexer.NOT_LIKE:
            return walkNotLike(node, node.getChild(0), node.getChild(1));
        case CmisQlStrictLexer.CONTAINS:
            if (node.getChildCount() == 1) {
                return walkContains(node, null, node.getChild(0));
            } else {
                return walkContains(node, node.getChild(0), node.getChild(1));
            }
        case CmisQlStrictLexer.IN_FOLDER:
            if (node.getChildCount() == 1) {
                return walkInFolder(node, null, node.getChild(0));
            } else {
                return walkInFolder(node, node.getChild(0), node.getChild(1));
            }
        case CmisQlStrictLexer.IN_TREE:
            if (node.getChildCount() == 1) {
                return walkInTree(node, null, node.getChild(0));
            } else {
                return walkInTree(node, node.getChild(0), node.getChild(1));
            }
        default:
            return walkOtherPredicate(node);
        }
    }

    /** For extensibility. */
    protected boolean walkOtherPredicate(Tree node) {
        throw new CmisRuntimeException("Unknown node type: " + node.getType() + " (" + node.getText() + ")");
    }

    public boolean walkNot(Tree opNode, Tree node) {
        walkPredicate(node);
        return false;
    }

    public boolean walkAnd(Tree opNode, Tree leftNode, Tree rightNode) {
        walkPredicate(leftNode);
        walkPredicate(rightNode);
        return false;
    }

    public boolean walkOr(Tree opNode, Tree leftNode, Tree rightNode) {
        walkPredicate(leftNode);
        walkPredicate(rightNode);
        return false;
    }

    public Object walkExpr(Tree node) {
        switch (node.getType()) {
        case CmisQlStrictLexer.BOOL_LIT:
            return walkBoolean(node);
        case CmisQlStrictLexer.NUM_LIT:
            return walkNumber(node);
        case CmisQlStrictLexer.STRING_LIT:
            return walkString(node);
        case CmisQlStrictLexer.TIME_LIT:
            return walkTimestamp(node);
        case CmisQlStrictLexer.IN_LIST:
            return walkList(node);
        case CmisQlStrictLexer.COL:
            return walkCol(node);
        default:
            return walkOtherExpr(node);
        }
    }

    /** For extensibility. */
    protected Object walkOtherExpr(Tree node) {
        throw new CmisRuntimeException("Unknown node type: " + node.getType() + " (" + node.getText() + ")");
    }

    public boolean walkEquals(Tree opNode, Tree leftNode, Tree rightNode) {
        walkExpr(leftNode);
        walkExpr(rightNode);
        return false;
    }

    public boolean walkNotEquals(Tree opNode, Tree leftNode, Tree rightNode) {
        walkExpr(leftNode);
        walkExpr(rightNode);
        return false;
    }

    public boolean walkGreaterThan(Tree opNode, Tree leftNode, Tree rightNode) {
        walkExpr(leftNode);
        walkExpr(rightNode);
        return false;
    }

    public boolean walkGreaterOrEquals(Tree opNode, Tree leftNode, Tree rightNode) {
        walkExpr(leftNode);
        walkExpr(rightNode);
        return false;
    }

    public boolean walkLessThan(Tree opNode, Tree leftNode, Tree rightNode) {
        walkExpr(leftNode);
        walkExpr(rightNode);
        return false;
    }

    public boolean walkLessOrEquals(Tree opNode, Tree leftNode, Tree rightNode) {
        walkExpr(leftNode);
        walkExpr(rightNode);
        return false;
    }

    public boolean walkIn(Tree opNode, Tree colNode, Tree listNode) {
        walkExpr(colNode);
        walkExpr(listNode);
        return false;
    }

    public boolean walkNotIn(Tree opNode, Tree colNode, Tree listNode) {
        walkExpr(colNode);
        walkExpr(listNode);
        return false;
    }

    public boolean walkInAny(Tree opNode, Tree colNode, Tree listNode) {
        walkExpr(colNode);
        walkExpr(listNode);
        return false;
    }

    public boolean walkNotInAny(Tree opNode, Tree colNode, Tree listNode) {
        walkExpr(colNode);
        walkExpr(listNode);
        return false;
    }

    public boolean walkEqAny(Tree opNode, Tree literalNode, Tree colNode) {
        walkExpr(literalNode);
        walkExpr(colNode);
        return false;
    }

    public boolean walkIsNull(Tree opNode, Tree colNode) {
        walkExpr(colNode);
        return false;
    }

    public boolean walkIsNotNull(Tree opNode, Tree colNode) {
        walkExpr(colNode);
        return false;
    }

    public boolean walkLike(Tree opNode, Tree colNode, Tree stringNode) {
        walkExpr(colNode);
        walkExpr(stringNode);
        return false;
    }

    public boolean walkNotLike(Tree opNode, Tree colNode, Tree stringNode) {
        walkExpr(colNode);
        walkExpr(stringNode);
        return false;
    }

    public boolean walkContains(Tree opNode, Tree qualNode, Tree queryNode) {
        if (qualNode != null)
            walkExpr(qualNode);
        walkExpr(queryNode);
        return false;
    }

    public boolean walkInFolder(Tree opNode, Tree qualNode, Tree paramNode) {
        if (qualNode != null)
            walkExpr(qualNode);
        walkExpr(paramNode);
        return false;
    }

    public boolean walkInTree(Tree opNode, Tree qualNode, Tree paramNode) {
        if (qualNode != null)
            walkExpr(qualNode);
        walkExpr(paramNode);
        return false;
    }

    public Object walkList(Tree node) {
        int n = node.getChildCount();
        List<Object> res = new ArrayList<Object>(n);
        for (int i = 0; i < n; i++) {
            res.add(walkExpr(node.getChild(i)));
        }
        return res;
    }

    public Object walkBoolean(Tree node) {
        String s = node.getText();
        return Boolean.valueOf(s);
    }

    public Object walkNumber(Tree node) {
        String s = node.getText();
        if (s.contains(".") || s.contains("e") || s.contains("E")) {
            return Double.valueOf(s);
        } else {
            return Long.valueOf(s);
        }
    }

    public Object walkString(Tree node) {
        String s = node.getText();
        s = s.substring(1, s.length() - 1);
        s = s.replace("''", "'"); // unescape quotes
        return s;
    }

    public Object walkTimestamp(Tree node) {
        String s = node.getText();
        s = s.substring(s.indexOf('\'') + 1, s.length() - 1);
        return CalendarHelper.fromString(s);
    }

    public Object walkCol(Tree node) {
        return null;
    }

}
/*
 ***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
 * Copyright (C) 2002 Jan Arne Petersen <jpetersen@uni-bonn.de>
 * Copyright (C) 2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
 * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
 * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/
package org.jruby.ast;

import org.jruby.Ruby;
import org.jruby.RubyRegexp;
import org.jruby.RubyString;
import org.jruby.ast.types.ILiteralNode;
import org.jruby.ast.visitor.NodeVisitor;
import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.runtime.Block;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.util.ByteList;

/**
 * A regexp which contains some expressions which will need to be evaluated everytime the regexp 
 * is used for a match.
 */
public class DRegexpNode extends DNode implements ILiteralNode {
    private final int options;
    private final boolean once;
    private RubyRegexp onceRegexp;
    private boolean is19;

    // 1.8 constructor
    public DRegexpNode(ISourcePosition position, int options, boolean once) {
        this(position, options, once, false);
    }

    // 1.9 constructor
    public DRegexpNode(ISourcePosition position, int options, boolean once, boolean is19) {
        super(position, null);
        this.once = once;
        this.options = options;
        this.is19 = is19;
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.DREGEXPNODE;
    }

    @Override
    protected RubyString allocateString(Ruby runtime) {
        return runtime.newString(new ByteList());
    }

    @Override
    public boolean is19() {
        return is19;
    }

    /**
     * Accept for the visitor pattern.
     * @param iVisitor the visitor
     **/
    @Override
    public Object accept(NodeVisitor iVisitor) {
        return iVisitor.visitDRegxNode(this);
    }

    /**
     * Gets the once.
     * @return Returns a boolean
     */
    public boolean getOnce() {
        return once;
    }

    /**
     * Gets the options.
     * @return Returns a int
     */
    public int getOptions() {
        return options;
    }

    /**
     * For regular expressions with /o flag
     * @return
     */
    public RubyRegexp getOnceRegexp() {
        return onceRegexp;
    }
    
    /**
     * For regular expressions with /o flag, the value in here can be used for subsequent evaluations.
     * Setting will only succeed if it is a regular expression with /o flag, and the value hasn't been already set.
     * @param regexp
     */
    public void setOnceRegexp(RubyRegexp regexp) {
        if (once && onceRegexp == null) this.onceRegexp = regexp;
    }
    
    @Override
    public IRubyObject interpret(Ruby runtime, ThreadContext context, IRubyObject self, Block aBlock) {
        if (once && onceRegexp != null) return onceRegexp;

        RubyString string = (RubyString) super.interpret(runtime, context, self, aBlock);
        RubyRegexp regexp = RubyRegexp.newDRegexp(runtime, string, options);
        
        if (once) setOnceRegexp(regexp);

        return regexp;
    }
}

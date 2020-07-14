package org.jruby.ir;

import java.util.EnumSet;

public enum IRFlags {
    // Does this scope require a binding to be materialized?
    // Yes if any of the following holds true:
    // - calls 'Proc.new'
    // - calls 'eval'
    // - calls 'call' (could be a call on a stored block which could be local!)
    // - calls 'send' and we cannot resolve the message (method name) that is being sent!
    // - calls methods that can access the caller's binding
    // - calls a method which we cannot resolve now!
    // - has a call whose closure requires a binding
    BINDING_HAS_ESCAPED,
    // Does this execution scope (applicable only to methods) receive a block and use it in such a way that
    // all of the caller's local variables need to be materialized into a heap binding?
    // Ex:
    //   def foo(&b)
    //      eval 'puts a', b
    //    end
    //
    //    def bar
    //      a = 1
    //      foo {} # prints out '1'
    //    end
    //
    // Here, 'foo' can access all of bar's variables because it captures the caller's closure.
    //
    // There are 2 scenarios when this can happen (even this is conservative -- but, good enough for now)
    // 1. This method receives an explicit block argument (in this case, the block can be stored, passed around,
    //   eval'ed against, called, etc.).
    //    CAVEAT: This is conservative ... it may not actually be stored & passed around, evaled, called, ...
    // 2. This method has a 'super' call (ZSuper AST node -- ZSuperInstr IR instruction)
    //    In this case, the parent (in the inheritance hierarchy) can access the block and store it, etc.  So, in reality,
    //    rather than assume that the parent will always do this, we can query the parent, if we can precisely identify
    //    the parent method (which in the face of Ruby's dynamic hierarchy, we cannot).  So, be pessimistic.
    //
    // This logic was extracted from an email thread on the JRuby mailing list -- Yehuda Katz & Charles Nutter
    // contributed this analysis above.
    ACCESS_PARENTS_LOCAL_VARIABLES, // Closure is capturing parent(s) variable state
    CAN_CAPTURE_CALLERS_BINDING,
    CAN_RECEIVE_BREAKS,           // may receive a break during execution
    CAN_RECEIVE_NONLOCAL_RETURNS, // may receive a non-local return during execution
    HAS_BREAK_INSTRS,             // contains at least one break
    HAS_END_BLOCKS,               // has an end block. big de-opt flag
    HAS_LOOPS,                    // has a loop
    HAS_NONLOCAL_RETURNS,         // has a non-local return
    MAYBE_USING_REFINEMENTS,      // a call to 'using' discovered...is it "the" using...maybe?
    RECEIVES_CLOSURE_ARG,         // This scope (or parent receives a closure
    RECEIVES_KEYWORD_ARGS,        // receives keyword args
    REQUIRES_DYNSCOPE,            // does this scope require a dynamic scope?
    USES_EVAL,                    // calls eval
    USES_ZSUPER,                  // has zsuper instr

    REQUIRES_LASTLINE,
    REQUIRES_BACKREF,
    REQUIRES_VISIBILITY,          // callee may read/write caller's visibility
    REQUIRES_BLOCK,
    REQUIRES_SELF,
    REQUIRES_METHODNAME,
    REQUIRES_LINE,
    REQUIRES_CLASS,
    REQUIRES_FILENAME,
    REQUIRES_SCOPE,

    DYNSCOPE_ELIMINATED,          // local var load/stores have been converted to tmp var accesses
    REUSE_PARENT_DYNSCOPE,        // for closures -- reuse parent's dynscope
    FLAGS_COMPUTED;               // Have these flags been computed yet?

    public static final EnumSet<IRFlags> REQUIRE_ALL_FRAME_FIELDS =
            EnumSet.of(
                    REQUIRES_LASTLINE,
                    REQUIRES_BACKREF,
                    REQUIRES_VISIBILITY,
                    REQUIRES_BLOCK,
                    REQUIRES_SELF,
                    REQUIRES_METHODNAME,
                    REQUIRES_LINE,
                    REQUIRES_CLASS,
                    REQUIRES_FILENAME,
                    REQUIRES_SCOPE);


    public static final EnumSet<IRFlags> REQUIRE_ALL_FRAME_EXCEPT_SCOPE =
            EnumSet.of(
                    REQUIRES_LASTLINE,
                    REQUIRES_BACKREF,
                    REQUIRES_VISIBILITY,
                    REQUIRES_BLOCK,
                    REQUIRES_SELF,
                    REQUIRES_METHODNAME,
                    REQUIRES_LINE,
                    REQUIRES_CLASS,
                    REQUIRES_FILENAME,
                    REQUIRES_SCOPE);
}

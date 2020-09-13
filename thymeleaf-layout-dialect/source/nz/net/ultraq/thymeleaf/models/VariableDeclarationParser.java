/*
 * Copyright 2016, Emanuel Rabina (http://www.ultraq.net.nz/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nz.net.ultraq.thymeleaf.models;

import java.util.ArrayList;
import java.util.List;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.AssignationUtils;

/**
 * Parser for variable declaration strings, which are the expressions that are
 * found withing {@code th:with} processors. This is really a wrapper around
 * Thymeleaf's {@link AssignationUtils} class, which is a crazy house of code
 * that splits the expression string into the parts needed by this dialect.
 *
 * @author zhanhb
 * @author Emanuel Rabina
 */
public class VariableDeclarationParser {

    private final IExpressionContext context;

    /**
     * Constructor, sets the processing context for the parser.
     *
     * @param context
     */
    public VariableDeclarationParser(IExpressionContext context) {
        this.context = context;
    }

    /**
     * Parse a variable declaration string, returning as many variable
     * declaration objects as there are variable declarations.
     *
     * @param declarationString
     * @return List of variable declaration objects.
     */
    public List<VariableDeclaration> parse(String declarationString) {
        AssignationSequence assignationSequence = AssignationUtils.parseAssignationSequence(context, declarationString, false);
        ArrayList<VariableDeclaration> list = new ArrayList<>(assignationSequence.size());
        for (Assignation assignation : assignationSequence) {
            list.add(new VariableDeclaration(assignation));
        }
        return list;
    }

}

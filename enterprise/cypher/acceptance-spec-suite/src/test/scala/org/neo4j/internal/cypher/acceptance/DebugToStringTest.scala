/*
 * Copyright (c) 2002-2018 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j Enterprise Edition. The included source
 * code can be redistributed and/or modified under the terms of the
 * GNU AFFERO GENERAL PUBLIC LICENSE Version 3
 * (http://www.fsf.org/licensing/licenses/agpl-3.0.html) with the
 * Commons Clause, as found in the associated LICENSE.txt file.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * Neo4j object code can be licensed independently from the source
 * under separate terms from the AGPL. Inquiries can be directed to:
 * licensing@neo4j.com
 *
 * More information is also available at:
 * https://neo4j.com/licensing/
 */
package org.neo4j.internal.cypher.acceptance

import org.neo4j.cypher.ExecutionEngineFunSuite
import org.neo4j.cypher.internal.util.v3_4.test_helpers.WindowsStringSafe


class DebugToStringTest extends ExecutionEngineFunSuite {

  implicit val windowsSafe = WindowsStringSafe

  /**
    * This tests an internal feature that is not supported or critical for end users. Still nice to see that it works
    * and what the expected outputs are.
    */

  test("ast-tostring works") {
    val textResult = graph.execute(queryWithOutputOf("ast")).resultAsString()

    textResult should equal("""+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
                              || col                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
                              |+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
                              || "Query(None,SingleQuery(List(Match(false,Pattern(List(EveryPath(RelationshipChain(NodePattern(Some(Variable(a)),List(),None),RelationshipPattern(Some(Variable(  UNNAMED10)),List(RelTypeName(T)),None,None,OUTGOING,false),NodePattern(Some(Variable(b)),List(),None))))),List(),None), Return(false,ReturnItems(false,Vector(AliasedReturnItem(Variable(a),Variable(a)), AliasedReturnItem(Variable(b),Variable(b)))),None,None,None,None,Set()))))" |
                              |+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
                              |1 row
                              |""".stripMargin)
  }

  test("logicalplan-tostring works") {
    val textResult = graph.execute(queryWithOutputOf("logicalPlan")).resultAsString()

    textResult should equal( """+-----------------------------------------------------------------------------------+
                               || col                                                                               |
                               |+-----------------------------------------------------------------------------------+
                               || "ProduceResult(Vector(a, b)) {"                                                   |
                               || "  LHS -> Expand(a, OUTGOING, List(RelTypeName(T)), b,   UNNAMED10, ExpandAll) {" |
                               || "    LHS -> AllNodesScan(a, Set()) {}"                                            |
                               || "  }"                                                                             |
                               || "}"                                                                               |
                               |+-----------------------------------------------------------------------------------+
                               |5 rows
                               |""".stripMargin)
  }

  test("queryGraph-tostring works") {
    val textResult = graph.execute(queryWithOutputOf("queryGraph")).resultAsString()

    textResult should equal("""+-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
                              || col                                                                                                                                                                                                                                       |
                              |+-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
                              || "UnionQuery(List(RegularPlannerQuery(QueryGraph {Nodes: ['a', 'b'], Rels: ['(a)--[  UNNAMED10:T]->-(b)']},RegularQueryProjection(Map(a -> Variable(a), b -> Variable(b)),QueryShuffle(List(),None,None)),None)),false,Vector(a, b),None)" |
                              |+-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
                              |1 row
                              |""".stripMargin)
  }

  test("semanticState-tostring works") {
    val textResult = graph.execute(queryWithOutputOf("semanticState")).resultAsString()

    // There's no good toString for SemanticState, which makes it difficult to test the output in a good way
    textResult should include("ScopeLocation")
  }

  private def queryWithOutputOf(s: String) = s"CYPHER debug=tostring debug=$s MATCH (a)-[:T]->(b) RETURN *"
}

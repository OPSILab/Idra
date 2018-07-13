/*******************************************************************************
 * Idra - Open Data Federation Platform
 *  Copyright (C) 2018 Engineering Ingegneria Informatica S.p.A.
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *  
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

ace.define("ace/mode/sparql", function(require, exports, module) {

var oop = require("../lib/oop");
var TextMode = require("./text").Mode;
var Tokenizer = require("../tokenizer").Tokenizer;
var SparqlHighlightRules = require("./sparql_highlight_rules").SparqlHighlightRules;
//var MatchingBraceOutdent = require("./matching_brace_outdent").MatchingBraceOutdent;
var Range = require("../range").Range;

var Mode = function() {
  this.$tokenizer = new Tokenizer(new SparqlHighlightRules().getRules());
};
oop.inherits(Mode, TextMode);

exports.Mode = Mode;

});
ace.define("ace/mode/sparql_highlight_rules", function(require, exports, module) {

var oop = require("../lib/oop");
var lang = require("../lib/lang");
var TextHighlightRules = require("./text_highlight_rules").TextHighlightRules;

var SparqlHighlightRules = function() {

  var builtinFunctions = lang.arrayToMap(
    "str|lang|langmatches|datatype|bound|sameterm|isiri|isuri|isblank|isliteral|union|a".split("|")
  );

  var keywords = lang.arrayToMap(
    ("base|BASE|prefix|PREFIX|select|SELECT|ask|ASK|construct|CONSTRUCT|describe|DESCRIBE|where|WHERE|"+
     "from|FROM|reduced|REDUCED|named|NAMED|order|ORDER|limit|LIMIT|offset|OFFSET|filter|FILTER|"+
     "optional|OPTIONAL|graph|GRAPH|by|BY|asc|ASC|desc|DESC").split("|")
  );

  var buildinConstants = lang.arrayToMap(
    "true|TRUE|false|FALSE".split("|")
  );

  var builtinVariables = lang.arrayToMap(
    ("").split("|")
  );

  // regexp must not have capturing parentheses. Use (?:) instead.
  // regexps are ordered -> the first match is used

  this.$rules = {
    "start" : [
      {
        token : "comment",
        regex : "#.*$"
      }, {
        token : "sparql.iri.constant.buildin",
        regex : "\\<\\S+\\>"
      }, {
        token : "sparql.variable",
        regex : "[\\?\\$][a-zA-Z]+"
      }, {
        token : "sparql.prefix.constant.language",
        regex : "[a-zA-Z]+\\:"
      }, {
        token : "string.regexp",
        regex : "[/](?:(?:\\[(?:\\\\]|[^\\]])+\\])|(?:\\\\/|[^\\]/]))*[/]\\w*\\s*(?=[).,;]|$)"
      }, {
        token : "string", // single line
        regex : '["](?:(?:\\\\.)|(?:[^"\\\\]))*?["]'
      }, {
        token : "string", // single line
        regex : "['](?:(?:\\\\.)|(?:[^'\\\\]))*?[']"
      }, {
        token : "constant.numeric", // hex
        regex : "0[xX][0-9a-fA-F]+\\b"
      }, {
        token : "constant.numeric", // float
        regex : "[+-]?\\d+(?:(?:\\.\\d*)?(?:[eE][+-]?\\d+)?)?\\b"
      }, {
        token : "constant.language.boolean",
        regex : "(?:true|false)\\b"
      }, {
        token : function(value) {
          if (value == "self")
            return "variable.language";
          else if (keywords.hasOwnProperty(value))
            return "keyword";
          else if (buildinConstants.hasOwnProperty(value))
            return "constant.language";
          else if (builtinVariables.hasOwnProperty(value))
            return "variable.language";
          else if (builtinFunctions.hasOwnProperty(value))
            return "support.function";
          else if (value == "debugger")
            return "invalid.deprecated";
          else
            return "identifier";
        },
        regex : "[a-zA-Z_$][a-zA-Z0-9_$]*\\b"
      }, {
        token : "keyword.operator",
        regex : "\\*|\\+|\\|\\-|\\<|\\>|=|&|\\|"
      }, {
        token : "lparen",
        regex : "[\\<({]"
      }, {
        token : "rparen",
        regex : "[\\>)}]"
      }, {
        token : "text",
        regex : "\\s+"
      }
    ],
    "comment" : [
      {
        token : "comment", // comment spanning whole line
        regex : ".+"
      }
    ]
  };
};

oop.inherits(SparqlHighlightRules, TextHighlightRules);
exports.SparqlHighlightRules = SparqlHighlightRules;
});

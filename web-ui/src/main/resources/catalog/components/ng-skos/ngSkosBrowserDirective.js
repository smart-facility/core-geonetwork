/**
 * @ngdoc directive
 * @name ng-skos.directive:skosBrowser
 * @restrict E
 * @scope
 * @description
 *
 * Provides a browsing interface to a concept scheme.
 *
 * The concept scheme must be provided as object with lookup functions to look
 * up a concept by URI (`lookupURI`), by notation (`lookupNotation`), and/or by
 * a unique preferred label (`lookupLabel`). Each lookup function, if given, 
 * must return an AngularJS promise to return a single concept in JSKOS format.
 * *Searching* in a concept scheme is not supported by this directive but one
 * can provide an additional OpenSearchSuggestion service (see
 * [ng-suggest](http://gbv.github.io/ng-suggest/)) with field `suggest`.
 *
 * ## Scope
 *
 * The following variables are added to the scope, if the corresponding
 * lookup function have been provided:
 *
 * <ul>
 * <li>selectURI
 * <li>selectNotation
 * <li>selectLabel
 * </ul>
 *
 * The current version only tries either one of this methods.
 *
 * The variable `loading` can be used to indicate loading delay.
 *
 * Suggestions are not fully supported yet.
 *
 * ## Customization
 *
 * The [default 
 * template](https://github.com/gbv/ng-skos/blob/master/src/templates/skos-browser.html)
 * can be changed with parameter `templateUrl`.
 *
 * ## Source code
 *
 * The most recent [source 
 * code](https://github.com/gbv/ng-skos/blob/master/src/directives/skosBrowser.js)
 * of this directive is available at GitHub.
 *
 * sppigot: Added thesaurus parameter to lookup functions api
 *
 * @param {string} concept selected [concept](http://gbv.github.io/jskos/jskos.html#concepts)
 * @param {string} concept-scheme object with lookup methods
 * @param {string} template-url URL of a template to display the concept browser
 */

(function() {
  goog.provide('ngSkos_browser_directive');
  goog.require('gn_skos_thesaurus_service');

	var module = angular.module('ngSkos_browser_directive', []);

	module.directive('skosBrowser', 
		[ '$compile', 'gnSkosThesaurusService', 
			function($compile, gnSkosThesaurusService) {

		var previous = [];

    return {
        restrict: 'E',
        replace: true,
        scope: { 
						concept: '=concept',
						addConcept: '=addConcept'
        },
        templateUrl: '../../catalog/components/ng-skos/templates/skos-browser.html',
        link: function link(scope, element, attr) {

						$compile(element.contents())(scope); // pick up skos concept
						                                     // directive with compiler

            angular.forEach(['URI','Notation','Label'],function(value){
                var lookup = gnSkosThesaurusService['lookup'+value];
                if (lookup) {
                    scope['select'+value] = function(thes, query) {
                        lookup(thes, query).then(
                            function(response) { 
																if (previous) response.previous = previous;
                                angular.copy(response, scope.concept);
                            }
                        );
                    };
                }
            });

						// Select concept for navigation, push previous concept onto stack
            scope.selectConcept = function(concept, previousConcept) {
								if (!previousConcept) previous.unshift(concept);
                if (scope.selectURI && concept.uri) {
                    scope.selectURI(concept.thesaurus, concept.uri);
                } else if (scope.selectNotation && concept.notation && concept.notation.length) {
                    scope.selectNotation(concept.thesaurus, concept.notation);
                } else if (scope.selectLabel && concept.prefLabel) {
                    scope.selectLabel(concept.thesaurus, concept.prefLabel);
                }
            };

						// Refresh current concept with top concept from thesaurus
						scope.topConcept = function(thesaurus) {
							gnSkosThesaurusService.getTopConcept(thesaurus).then(
								function(c) {
									angular.copy(c, scope.concept);
								});
						};

        }
     }
	}]);

})();

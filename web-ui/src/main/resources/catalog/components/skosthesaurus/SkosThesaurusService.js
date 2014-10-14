(function() {
  goog.provide('gn_skos_thesaurus_service');

  var module = angular.module('gn_skos_thesaurus_service', []);

	module.value('gnThesaurusTopConcepts', {
		'external.theme.gcmd_keywords': 'http://gcmdservices.gsfc.nasa.gov/kms/concept/e9f67a66-e9fc-435c-b720-ae32a2c3d8f5'
	});

  module.factory('Keyword', function() {
    function Keyword(k) {
      this.props = $.extend(true, {}, k);
      this.label = this.getLabel();
			this.tagClass = 'label label-info gn-line-height';
    };
    Keyword.prototype = {
      getId: function() {
        return this.props.uri;
      },
      getLabel: function() {
        return this.props.value['#text'];
      }
    };

    return Keyword;
  });

  module.factory('Thesaurus', function() {
    function Thesaurus(k) {
      this.props = $.extend(true, {}, k);
    };
    Thesaurus.prototype = {
      getKey: function() {
        return this.props.key;
      },
      getTitle: function() {
        return this.props.title;
      }
    };

    return Thesaurus;
  });

  module.provider('gnSkosThesaurusService',
      function() {
        this.$get = [
          '$q',
          '$rootScope',
          '$http',
          'gnUrlUtils',
          'Keyword',
          'Thesaurus',
					'gnThesaurusTopConcepts',
          function($q, $rootScope, $http, gnUrlUtils, Keyword, Thesaurus, 
									 gnThesaurusTopConcepts) {
            var getKeywordsSearchUrl = function(filter, 
                thesaurus, max, typeSearch) {
              return gnUrlUtils.append('keywords@json',
                  gnUrlUtils.toKeyValue({
                    pNewSearch: 'true',
                    pTypeSearch: typeSearch || 1,
                    pThesauri: thesaurus,
                    pMode: 'searchBox',
                    maxResults: max,
                    pKeyword: filter || ''
                  })
              );
            };
            var parseKeywordsResponse = function(data) {
              var listOfKeywords = [];
              angular.forEach(data[0], function(k) {
                listOfKeywords.push(new Keyword(k));
              });
              return listOfKeywords;
            };

						// Drive JSKOS API with these functions
    				function getConcept(thesaurus, keywordUris) {
								var defer = $q.defer();
        				var url = gnUrlUtils.append('thesaurus.keyword.jskos@json',
           				gnUrlUtils.toKeyValue({
             				thesaurus: thesaurus,
             				id: keywordUris instanceof Array ?
             				keywordUris.join(',') : keywordUris || ''
            				})
        				);
        				$http.get(url, { cache: true }).
           				success(function(data, status) {
             				defer.resolve(data);
           				}).
           				error(function(data, status) {
             				//                TODO handle error
             				//                defer.reject(error);
           				});
        				return defer.promise;
    				};

    				// expected to promise one JSKOS concept
    				// [concept](http://gbv.github.io/jskos/jskos.html#concepts)
						var lookupURI = function(thesaurus, keywordUri) {
							var deferred = $q.defer();
      				// first get concept, then get links to other concepts
							getConcept(thesaurus, keywordUri).then(function(c) {
								deferred.resolve(c);
							});
							return deferred.promise;
						};
				
    				// expected to promise an array of JSKOS concepts
    				// [concept](http://gbv.github.io/jskos/jskos.html#concepts)
    				var getTopConcept = function(thesaurus) {
								console.log("Looking for concepts in "+thesaurus+", found: "+thesaurusTopConcepts[thesaurus]);
        				return thesaurusTopConcepts[thesaurus];
    				};

						var thesaurusTopConcepts = [];

            return {
							/**
							 * Get All thesaurus top concepts ready for editor to use 
							 */
							getThesaurusTopConcepts: function() {
								var deferred = $q.defer();
								var gets = [];
								for (var thesaurus in gnThesaurusTopConcepts) {
									gets.push(
										getConcept(thesaurus, gnThesaurusTopConcepts[thesaurus])
									);
								}
								$q.all(gets).then(function() {
									for (var i = 0; i < arguments.length; i++) {
										if (arguments[i][0].thesaurus) {
											thesaurusTopConcepts[arguments[i][0].thesaurus] = arguments[i][0];
										}
									}
									deferred.resolve();
								});
								return deferred.promise;
							},
              /**
               * Number of keywords returned by search (autocompletion
               * or selection, ...)
               */
              DEFAULT_NUMBER_OF_RESULTS: 200,
              /**
               * Number of keywords to display in autocompletion list
               */
              DEFAULT_NUMBER_OF_SUGGESTIONS: 30,
              /**
               * Request the XML for the thesaurus and its keywords
               * in a specific format (based on the transformation).
               *
               * eg. to-iso19139-keyword for default form.
               */
              getXML: function(thesaurus, 
                  keywordUris, transformation) {
                // http://localhost:8080/geonetwork/srv/eng/
                // xml.keyword.get?thesaurus=external.place.regions&id=&
                // multiple=false&transformation=to-iso19139-keyword&
                var defer = $q.defer();
                var url = gnUrlUtils.append('thesaurus.keyword',
                    gnUrlUtils.toKeyValue({
                      thesaurus: thesaurus,
                      id: keywordUris instanceof Array ?
                          keywordUris.join(',') : keywordUris || '',
                      multiple: keywordUris instanceof Array ? 'true' : 'false',
                      transformation: transformation || 'to-iso19139-keyword'
                    })
                    );
                $http.get(url, { cache: true }).
                    success(function(data, status) {
                      // TODO: could be a global constant ?
                      var xmlDeclaration =
                          '<?xml version="1.0" encoding="UTF-8"?>';
                      defer.resolve(data.replace(xmlDeclaration, ''));
                    }).
                    error(function(data, status) {
                      //                TODO handle error
                      //                defer.reject(error);
                    });
                return defer.promise;
              },
              /**
               * Get thesaurus list.
               */
              getAll: function(schema) {
                var defer = $q.defer();
                $http.get('thesaurus@json?' +
                    'element=gmd:descriptiveKeywords&schema=' +
                    (schema || 'iso19139'), { cache: true }).
                    success(function(data, status) {
                      var listOfThesaurus = [];
                      angular.forEach(data[0], function(k) {
                        listOfThesaurus.push(new Thesaurus(k));
                      });
                      defer.resolve(listOfThesaurus);
                    }).
                    error(function(data, status) {
                      //                TODO handle error
                      //                defer.reject(error);
                    });
                return defer.promise;

              },
              getKeywordsSearchUrl: getKeywordsSearchUrl,
              parseKeywordsResponse: parseKeywordsResponse,
              getKeywords: function(filter, thesaurus, max, typeSearch) {
                var defer = $q.defer();
                var url = getKeywordsSearchUrl(filter,
                    thesaurus, max, typeSearch);
                $http.get(url, { cache: true }).
                    success(function(data, status) {
                      defer.resolve(parseKeywordsResponse(data));
                    }).
                    error(function(data, status) {
                      //                TODO handle error
                      //                defer.reject(error);
                    });
                return defer.promise;
              },

							// ConceptScheme API for JSKOS
        			lookupURI: lookupURI,
        			lookupNotation: null,
        			lookupLabel: null,
        			getTopConcept: getTopConcept,
        			suggest: null
            };
          }];
      });
})();

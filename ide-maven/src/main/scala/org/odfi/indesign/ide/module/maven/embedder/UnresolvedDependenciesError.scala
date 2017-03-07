package org.odfi.indesign.ide.module.maven.embedder


import com.idyria.osi.tea.errors.TError
import org.eclipse.aether.graph.Dependency

class UnresolvedDependenciesError(val deps : List[Dependency]) extends TError("Some dependencies were not resolved") {
  
}
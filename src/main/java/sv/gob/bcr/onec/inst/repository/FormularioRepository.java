package sv.gob.bcr.onec.inst.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.gob.bcr.onec.inst.entity.Formulario;

public interface FormularioRepository extends JpaRepository<Formulario, Integer> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdFormularioNot(String codigo, Integer idFormulario);
}

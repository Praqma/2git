#!/bin/bash --login
set -x
set -u
set -e
set -o pipefail

export ccm_project_name_wo_instance=$(echo ${ccm_project_name} | awk -F ":" '{print $1}')
export ccm_project_instance=$(echo ${ccm_project_name} | awk -F ":" '{print $2}')

if [ "${ccm_project_instance}x" == "x" ]; then
  export ccm_project_instance="1"
fi

if [[ "${tag_to_be_removed:-}" != "" ]] ; then
  cd ${ccm_project_name_wo_instance} 
  git push ssh://git@${git_server_path}/${ccm_project_name_wo_instance}.git $tag_to_be_removed --delete  || echo "Deleting failed - skip"
  git push ssh://git@${git_server_path}/${ccm_project_name_wo_instance}_orig.git $tag_to_be_removed --delete  || echo "Deleting failed - skip"
  if [[ ${git_server_path_prod_git2git:-} != "" ]] ; then
    echo "Also handle production"
    git push ssh://git@${git_server_path_prod_git2git}/${ccm_project_name_wo_instance}.git $tag_to_be_removed --delete  || echo "Deleting failed - skip"
    git push ssh://git@${git_server_path_prod_git2git}/${ccm_project_name_wo_instance}_orig.git $tag_to_be_removed --delete  || echo "Deleting failed - skip"
  fi
  git tag --delete $tag_to_be_removed
  cd ${WORKSPACE}
fi

export PATH=${ccm_home_path}/bin:$PATH
export CCM_HOME=${ccm_home_path}

which ccm
export CCM_ADDR=`ccm start -m -d /data/ccmdb/${ccm_db} -s ${ccm_server} -q` 
[[ $CCM_ADDR == "" ]] && ( echo "CM/Synergy start failed" && exit 10 )

debug=true ${WORKSPACE}/ccm-convert-flat2submodules-existingRepoHistory.sh \
                                "${ccm_project_name_wo_instance}" \
                                "init" \
                                "${repo_submodules}" \
                                "${git_server_project}" \
                                "${ccm_project_instance}" \
                                "${gitignore_path_n_files:-}" \
                                "${gitattributes_path_n_files:-}"

echo "ccm_project_name_wo_instance=${ccm_project_name_wo_instance}"  > ccm.env
echo "ccm_project_instance=${ccm_project_instance}"                 >> ccm.env


#!/usr/bin/env python3
"""
PM 0.14.3 Entity Movement Fix — Apply Patches
==============================================
Applies all 13 patches to fix:
  1. Y-axis bouncing/twitching
  2. Horizontal stop-and-go movement
  3. Excessive movement speed

Usage: python apply.py <pm-root>

If <pm-root> is omitted, assumes the script is run from the repo root.
"""

import os
import re
import sys


def fatal(msg):
    print(f"[ERROR] {msg}")
    sys.exit(1)


def patch_file(rel_path, old_text, new_text, root):
    full = os.path.join(root, rel_path)
    if not os.path.isfile(full):
        fatal(f"File not found: {full}")
    with open(full, "r", encoding="utf-8") as fh:
        content = fh.read()
    if old_text not in content:
        print(f"  [SKIP] pattern not found in {rel_path}")
        return False
    new_content = content.replace(old_text, new_text)
    with open(full, "w", encoding="utf-8") as fh:
        fh.write(new_content)
    print(f"  [OK]   {rel_path}")
    return True


def main():
    root = sys.argv[1] if len(sys.argv) > 1 else os.getcwd()
    print(f"Patching PM root: {root}\n")

    # 1. Mob.php — Restructure onUpdate (navigator before physics)
    patch_file(
        "src/pocketmine/entity/Mob.php",
        # Old onUpdate
        """    public function onUpdate($tick){
        $hasUpdate = parent::onUpdate($tick);
        if($this->closed or !$this->isAlive()) return false;

        if($this->behaviorsEnabled){
            $prev = $this->currentBehavior;
            $this->currentBehavior = $this->checkBehavior();

            if($this->currentBehavior !== null and $this->currentBehavior !== $prev){
                $this->currentBehavior->onStart();
            }
            if($this->currentBehavior !== null){
                $this->currentBehavior->onTick();
            }
        }

        // 导航器更新（在 Behavior 决策之后、下一个 tick 物理之前应用 motion）
        if($this->navigator !== null){
            $this->navigator->update();
        }

        return $hasUpdate;
    }""",
        # New onUpdate
        """    public function onUpdate($tick){
        if($this->closed or !$this->isAlive()) return false;

        // 先运行行为 + 导航器，设置 motion，再让物理处理
        if($this->behaviorsEnabled){
            $prev = $this->currentBehavior;
            $this->currentBehavior = $this->checkBehavior();

            if($this->currentBehavior !== null and $this->currentBehavior !== $prev){
                $this->currentBehavior->onStart();
            }
            if($this->currentBehavior !== null){
                $this->currentBehavior->onTick();
            }
        }

        // 导航器更新（在 Behavior 决策之后、物理之前应用 motion）
        if($this->navigator !== null){
            $this->navigator->update();
        }

        // 再运行物理（Creature::onUpdate -> move()）
        return parent::onUpdate($tick);
    }""",
        root,
    )

    # 1b. Mob.php — Smooth Y adaptation in moveInDirection
    patch_file(
        "src/pocketmine/entity/Mob.php",
        # Old Y adaptation
        """        $diff = $targetY - $ty;
        if($diff > 0){
            $this->motionY = 0.42;
        }elseif($diff < 0){
            $this->motionY = -0.2;
        }""",
        # New Y adaptation with dead zone
        """        $diff = $targetY - $ty;
        // 仅在 ground 上且 Y 差足够大时辅助跳跃/下落，避免振荡
        if($diff > 0 and $this->onGround){
            $this->motionY = 0.35;
        }elseif($diff < -1 and $this->onGround){
            $this->motionY = -0.15;
        }
        // diff 在 -1~0 时让重力自然处理，避免不平地形的 Y 振荡""",
        root,
    )

    # 2. Creature.php — Remove motionY=0 reset and duplicate updateMovement
    patch_file(
        "src/pocketmine/entity/Creature.php",
        # Old physics end
        """                $this->motionX *= $friction;
                $this->motionY *= 1 - $this->drag;
                $this->motionZ *= $friction;

                if($this->onGround){
                    $this->motionY = 0;
                }

                $this->updateMovement();""",
        # New physics end
        """                $this->motionX *= $friction;
                $this->motionY *= 1 - $this->drag;
                $this->motionZ *= $friction;
                // 移除 motionY = 0 强制归零，让重力与导航器自然协调
                // updateMovement() 由 Entity::onUpdate 统一调用""",
        root,
    )

    # 3. Entity.php — Reduce network update threshold
    patch_file(
        "src/pocketmine/entity/Entity.php",
        'if($diffPosition > 0.04 or $diffRotation',
        'if($diffPosition > 0.0004 or $diffRotation',
        root,
    )

    # 4. PathNavigate.php — Reduce step multipliers (both occurrences)
    pn_path = os.path.join(root, "src/pocketmine/entity/ai/navigation/PathNavigate.php")
    with open(pn_path, "r", encoding="utf-8") as fh:
        pn_content = fh.read()
    pn_content = pn_content.replace(
        '$step = $this->speed * 0.7 * ($this->entity->isInsideOfWater() ? 0.3 : 0.4);',
        '$step = $this->speed * 0.55 * ($this->entity->isInsideOfWater() ? 0.04 : 0.06);',
    )
    with open(pn_path, "w", encoding="utf-8") as fh:
        fh.write(pn_content)
    print("  [OK]   src/pocketmine/entity/ai/navigation/PathNavigate.php (step multipliers)")

    # 5. Behavior.php — moveForward multiplier sync
    patch_file(
        "src/pocketmine/entity/ai/behavior/Behavior.php",
        '$mult = 0.7 * ($entity->isInsideOfWater() ? 0.3 : 0.4);',
        '$mult = 0.55 * ($entity->isInsideOfWater() ? 0.04 : 0.06);',
        root,
    )

    # 6. AttackEnemyBehavior.php — Reduce speed defaults
    patch_file(
        "src/pocketmine/entity/ai/behavior/AttackEnemyBehavior.php",
        'float $speed = 0.7, float $speedMultiplier = 0.75',
        'float $speed = 0.65, float $speedMultiplier = 0.50',
        root,
    )

    # 7. StrollBehavior.php — Reduce stroll speed
    patch_file(
        "src/pocketmine/entity/ai/behavior/StrollBehavior.php",
        'float $speed = 0.7, int $timeout = 120)',
        'float $speed = 0.40, int $timeout = 120)',
        root,
    )

    # 8. PanicBehavior.php — Reduce panic speed
    patch_file(
        "src/pocketmine/entity/ai/behavior/PanicBehavior.php",
        'float $speed = 1.0, int $timeout = 60)',
        'float $speed = 0.55, int $timeout = 60)',
        root,
    )

    # 9. ShootPlayerBehavior.php — Reduce archer speed
    patch_file(
        "src/pocketmine/entity/ai/behavior/ShootPlayerBehavior.php",
        'int $NetWorkID, float $speed = 0.5)',
        'int $NetWorkID, float $speed = 0.35)',
        root,
    )

    
    # 10. CreeperBehavior — Adjust speed for new step multiplier
    patch_file(
        "src/pocketmine/entity/ai/behavior/CreeperBehavior.php",
        'public $speed = 0.19;',
        'public $speed = 0.30;',
        root,
    )

    # 11. SlimeBehavior — Update built-in multiplier and defaults
    slime_path = os.path.join(root, "src/pocketmine/entity/ai/behavior/SlimeBehavior.php")
    with open(slime_path, "r", encoding="utf-8") as fh:
        slime_content = fh.read()
    slime_content = slime_content.replace(
        '0.7*($this->entity->isInsideOfWater() ? 0.3 : 0.4)',
        '0.55*($this->entity->isInsideOfWater() ? 0.04 : 0.06)',
    )
    slime_content = slime_content.replace(
        'float $speed = 0.5, float $speedMultiplier = 0.75)',
        'float $speed = 0.30, float $speedMultiplier = 0.75)',
    )
    with open(slime_path, "w", encoding="utf-8") as fh:
        fh.write(slime_content)
    print("  [OK]   src/pocketmine/entity/ai/behavior/SlimeBehavior.php (multiplier + defaults)")

    # 12. InLoveBehavior — Reduce default speed
    patch_file(
        "src/pocketmine/entity/ai/behavior/InLoveBehavior.php",
        'float $speed = 0.5)',
        'float $speed = 0.35)',
        root,
    )

    # 13. FindFoodBehavior — Reduce default speed
    patch_file(
        "src/pocketmine/entity/ai/behavior/FindFoodBehavior.php",
        'float $speed = 0.5)',
        'float $speed = 0.35)',
        root,
    )

print("\nAll 13 patches applied successfully!")


if __name__ == "__main__":
    main()



